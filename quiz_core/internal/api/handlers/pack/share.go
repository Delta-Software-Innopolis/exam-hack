package pack_handlers

import (
	"errors"
	"net/http"

	sc "quiz_core/internal/api/shortcuts"
	"quiz_core/internal/db"
	"quiz_core/internal/models"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func GetSharedPack(c *gin.Context) {
	shareCode := c.Param("share_code")
	if shareCode == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "share code is required"})
		return
	}

	var pack models.Pack
	if err := db.DB.
		Preload("Author").
		Preload("Cards", func(tx *gorm.DB) *gorm.DB {
			return tx.Order("id ASC")
		}).
		Preload("Cards.Options", func(tx *gorm.DB) *gorm.DB {
			return tx.Order("id ASC")
		}).
		Where("share_code = ?", shareCode).
		First(&pack).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			c.JSON(http.StatusNotFound, gin.H{"error": "pack not found"})
			return
		}

		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to get shared pack"})
		return
	}

	c.JSON(http.StatusOK, sc.PackWithCardsResponse(pack))
}

func AddSharedPack(c *gin.Context) {
	shareCode := c.Param("share_code")
	if shareCode == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "share code is required"})
		return
	}

	userID, ok := sc.CurrentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	var pack models.Pack
	if err := db.DB.Transaction(func(tx *gorm.DB) error {
		if err := tx.
			Preload("Author").
			Preload("Cards", func(tx *gorm.DB) *gorm.DB {
				return tx.Order("id ASC")
			}).
			Preload("Cards.Options", func(tx *gorm.DB) *gorm.DB {
				return tx.Order("id ASC")
			}).
			Where("share_code = ?", shareCode).
			First(&pack).Error; err != nil {
			return err
		}

		if pack.AuthorID == userID {
			return errAuthorCannotAddOwnPack
		}

		var existingPermission models.PackPermission
		err := tx.
			Where("user_id = ? AND pack_id = ?", userID, pack.ID).
			First(&existingPermission).Error
			
		if err == nil {
			return errPackAlreadyAdded
		}

		if !errors.Is(err, gorm.ErrRecordNotFound) {
			return err
		}

		permission := models.PackPermission{
			UserID:     userID,
			PackID:     pack.ID,
			Permission: 2,
		}

		return tx.Create(&permission).Error
	}); err != nil {
		switch {
		case errors.Is(err, gorm.ErrRecordNotFound):
			c.JSON(http.StatusNotFound, gin.H{"error": "pack not found"})
		case errors.Is(err, errAuthorCannotAddOwnPack):
			c.JSON(http.StatusForbidden, gin.H{"error": "author cannot add own pack"})
		case errors.Is(err, errPackAlreadyAdded):
			c.JSON(http.StatusConflict, gin.H{"error": "pack already added"})
		default:
			c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to add shared pack"})
		}
		return
	}

	c.JSON(http.StatusOK, sc.PackWithCardsResponse(pack))
}

var (
	errAuthorCannotAddOwnPack = errors.New("author cannot add own pack")
	errPackAlreadyAdded       = errors.New("pack already added")
)
