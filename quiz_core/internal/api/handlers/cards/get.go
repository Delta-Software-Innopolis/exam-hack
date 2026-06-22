package cards_handlers

import (
	"net/http"
	"quiz_core/internal/db"
	"quiz_core/internal/models"
	"strconv"

	structs "quiz_core/internal/api/structures"
	sc "quiz_core/internal/api/shortcuts"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func GetCards(c *gin.Context) {
	packID, err := strconv.ParseUint(c.Param("pack_id"), 10, 0)
	if err != nil || packID == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid pack id"})
		return
	}

	var cards []models.Card

	if err := db.DB.
		Preload("Options", func(tx *gorm.DB) *gorm.DB {
			return tx.Order("id ASC")
		}).
		Where("pack_id = ?", packID).
		Order("id ASC").
		Find(&cards).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to get cards"})

		return
	}

	response := structs.CardsResponse{
		Cards: sc.CardResponses(cards),
	}

	c.JSON(http.StatusOK, response)
}