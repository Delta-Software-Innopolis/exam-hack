package cards_handlers

import (
	"errors"
	"net/http"
	"strings"

	sc "quiz_core/internal/api/shortcuts"
	structs "quiz_core/internal/api/structures"
	"quiz_core/internal/db"
	"quiz_core/internal/models"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)


func UpdateCards(c *gin.Context) {
	var req structs.UpdateCardsRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "cards are required"})
		return
	}

	if len(req.Cards) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "cards are required"})
		return
	}

	if err := sc.ValidateUpdateCardsRequest(req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	authorID, ok := sc.CurrentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	updatedCards := make([]models.Card, 0, len(req.Cards))
	
	err := db.DB.Transaction(func(tx *gorm.DB) error {
		for _, cardReq := range req.Cards {
			card, err := sc.FindOwnedCard(tx, cardReq.ID, authorID)
			if err != nil {
				return err
			}

			if cardReq.Question != nil {
				card.Question = strings.TrimSpace(*cardReq.Question)
			}

			if cardReq.Hint != nil {
				card.Hint = strings.TrimSpace(*cardReq.Hint)
			}

			if cardReq.Question != nil || cardReq.Hint != nil {
				if err := tx.Save(&card).Error; err != nil {
					return err
				}
			}

			if cardReq.Options != nil {
				options, err := sc.ReplaceCardOptions(tx, card.ID, *cardReq.Options, cardReq.Correct)
				if err != nil {
					return err
				}
				card.Options = options
			} else if cardReq.Correct != nil {
				options, err := sc.UpdateCardCorrectOptions(tx, card.ID, *cardReq.Correct)
				if err != nil {
					return err
				}
				card.Options = options
			} else if err := tx.
				Where("card_id = ?", card.ID).
				Order("id ASC").
				Find(&card.Options).Error; err != nil {
				return err
			}

			updatedCards = append(updatedCards, card)
		}

		return nil
	})

	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			c.JSON(http.StatusNotFound, gin.H{"error": "card not found"})
			return
		}

		if errors.Is(err, sc.ErrCorrectOptionIndexOutOfRange) {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}

		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to update cards"})
		return
	}

	c.JSON(http.StatusOK, structs.CardsResponse{Cards: sc.CardResponses(updatedCards)})
}