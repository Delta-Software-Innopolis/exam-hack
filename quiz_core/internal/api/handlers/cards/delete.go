package cards_handlers

import (
	"errors"
	"net/http"

	sc "quiz_core/internal/api/shortcuts"
	structs "quiz_core/internal/api/structures"
	"quiz_core/internal/db"
	"quiz_core/internal/models"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func DeleteCard(c *gin.Context) {
	var req structs.DeleteCardsRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "cards are required"})
		return
	}

	if len(req.Cards) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "cards are required"})
		return
	}

	authorID, ok := sc.CurrentUserID(c)

	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	err := db.DB.Transaction(func(tx *gorm.DB) error {
		for _, cardID := range req.Cards {
			card, err := sc.FindOwnedCard(tx, uint(cardID), authorID)
			if err != nil {
				return err
			}

			if err := tx.Where("card_id = ?", card.ID).Delete(&models.CardOption{}).Error; err != nil {
				return err
			}

			if err := tx.Delete(&card).Error; err != nil {
				return err
			}
		}

		return nil
	})

	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			c.JSON(http.StatusNotFound, gin.H{"error": "card not found"})
			return
		}

		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to delete card"})
		return
	}

	c.Status(http.StatusOK)
}
