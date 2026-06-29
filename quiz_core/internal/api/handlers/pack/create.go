package pack_handlers

import (
	"net/http"
	"time"

	sc "quiz_core/internal/api/shortcuts"
	structs "quiz_core/internal/api/structures"
	"quiz_core/internal/db"

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

	authorID, ok := sc.CurrentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	var packID uint
	var packShareCode string
	var packCreationDate time.Time

	if err := db.DB.Transaction(func(tx *gorm.DB) error {
		pack, err := sc.CreatePackWithOwnerPermission(tx, name, authorID)
		if err != nil {
			return err
		}

		packID = pack.ID
		packShareCode = pack.ShareCode
		packCreationDate = pack.CreationDate
		return nil
	}); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create pack"})
		return
	}

	c.JSON(http.StatusCreated, structs.PackResponse{
		ID:           packID,
		Name:         name,
		CreationDate: packCreationDate,
		ShareCode:    packShareCode,
		Author:       sc.CurrentUserResponse(c, authorID),
	})
}
