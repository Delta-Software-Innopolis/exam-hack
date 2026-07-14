package cards_handlers

import (
	"errors"
	"net/http"
	sc "quiz_core/internal/api/shortcuts"
	structs "quiz_core/internal/api/structures"
	"quiz_core/internal/db"
	"quiz_core/internal/models"
	"strconv"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func CreateCards(c *gin.Context) {
	packID, err := strconv.ParseUint(c.Param("pack_id"), 10, 0)

	if err != nil || packID == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid pack id"})
		return
	}

	var req structs.CreateCardsRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "cards are required"})
		return
	}

	if len(req.Cards) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "cards are required"})
		return
	}

	if err := sc.ValidateCreateCardsRequest(req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	authorID, ok := sc.CurrentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	if err := sc.EnsurePackOwned(uint(packID), authorID); err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			c.JSON(http.StatusNotFound, gin.H{"error": "pack not found"})
			return
		}

		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create cards"})
		return
	}

	var createdCards []models.Card

	err = db.DB.Transaction(func(tx *gorm.DB) error {
		var err error
		createdCards, err = sc.CreateCardsForPack(tx, uint(packID), req.Cards)
		return err
	})

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create cards"})
		return
	}

	c.JSON(http.StatusOK, structs.CardsResponse{Cards: sc.CardResponses(createdCards)})
}
