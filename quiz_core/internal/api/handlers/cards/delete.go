package cards_handlers

import (
	"errors"
	"net/http"
	"strconv"

	sc "quiz_core/internal/api/shortcuts"
	"quiz_core/internal/db"
	"quiz_core/internal/models"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)


func DeleteCard(c *gin.Context) {
	cardID, err := strconv.ParseUint(c.Param("card_id"), 10, 0)

	if err != nil || cardID == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid card id"})
		return
	}

	authorID, ok := sc.CurrentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	err = db.DB.Transaction(func(tx *gorm.DB) error {
		card, err := sc.FindOwnedCard(tx, uint(cardID), authorID)
		if err != nil {
			return err
		}

		if err := tx.Where("card_id = ?", card.ID).Delete(&models.CardOption{}).Error; err != nil {
			return err
		}

		return tx.Delete(&card).Error
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
