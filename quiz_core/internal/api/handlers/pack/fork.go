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

func ForkPack(c *gin.Context) {
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

	var forkedPack models.Pack
	err := db.DB.Transaction(func(tx *gorm.DB) error {
		var originalPack models.Pack
		if err := tx.
			Preload("Cards", func(tx *gorm.DB) *gorm.DB {
				return tx.Order("id ASC")
			}).
			Preload("Cards.Options", func(tx *gorm.DB) *gorm.DB {
				return tx.Order("id ASC")
			}).
			Where("share_code = ?", shareCode).
			First(&originalPack).Error; err != nil {
			return err
		}

		newPack, err := sc.CreatePackWithOwnerPermission(tx, originalPack.Name, userID)
		if err != nil {
			return err
		}

		fork := models.Fork{
			ForkID:     newPack.ID,
			OriginalID: originalPack.ID,
		}
		if err := tx.Create(&fork).Error; err != nil {
			return err
		}

		for _, originalCard := range originalPack.Cards {
			card := models.Card{
				Question: originalCard.Question,
				Hint:     originalCard.Hint,
				PackID:   newPack.ID,
			}

			if err := tx.Create(&card).Error; err != nil {
				return err
			}

			options := make([]models.CardOption, 0, len(originalCard.Options))
			for _, originalOption := range originalCard.Options {
				options = append(options, models.CardOption{
					Content: originalOption.Content,
					IsRight: originalOption.IsRight,
					CardID:  card.ID,
				})
			}

			if len(options) > 0 {
				if err := tx.Create(&options).Error; err != nil {
					return err
				}
			}
		}

		return tx.
			Preload("Author").
			Preload("Cards", func(tx *gorm.DB) *gorm.DB {
				return tx.Order("id ASC")
			}).
			Preload("Cards.Options", func(tx *gorm.DB) *gorm.DB {
				return tx.Order("id ASC")
			}).
			First(&forkedPack, newPack.ID).Error
	})
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			c.JSON(http.StatusNotFound, gin.H{"error": "pack not found"})
			return
		}

		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to fork pack"})
		return
	}

	c.JSON(http.StatusCreated, sc.PackWithCardsResponse(forkedPack))
}
