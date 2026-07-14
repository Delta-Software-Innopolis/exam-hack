package pack_handlers

import (
	"net/http"

	sc "quiz_core/internal/api/shortcuts"
	structs "quiz_core/internal/api/structures"
	"quiz_core/internal/db"
	"quiz_core/internal/models"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func CreatePack(c *gin.Context) {
	var req structs.CreatePackRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "pack name is required"})
		return
	}

	name, ok := sc.PackName(c, req.Name)
	if !ok {
		return
	}

	if len(req.Cards) > 0 {
		if err := sc.ValidateCreateCardsRequest(structs.CreateCardsRequest{Cards: req.Cards}); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
	}

	authorID, ok := sc.CurrentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	var pack models.Pack
	var createdCards []models.Card

	if err := db.DB.Transaction(func(tx *gorm.DB) error {
		createdPack, err := sc.CreatePackWithOwnerPermission(tx, name, req.Description, authorID)
		if err != nil {
			return err
		}

		pack = createdPack

		if len(req.Cards) > 0 {
			createdCards, err = sc.CreateCardsForPack(tx, pack.ID, req.Cards)
			if err != nil {
				return err
			}
		}

		return nil
	}); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create pack"})
		return
	}

	c.JSON(http.StatusCreated, structs.PackWithCardsResponse{
		ID:           pack.ID,
		Name:         pack.Name,
		Description:  pack.Description,
		CreationDate: pack.CreationDate,
		ShareCode:    pack.ShareCode,
		Author:       sc.CurrentUserResponse(c, authorID),
		Cards:        sc.CardResponses(createdCards),
	})
}
