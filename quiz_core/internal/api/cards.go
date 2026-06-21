package api

import (
	"errors"
	"net/http"
	"quiz_core/internal/db"
	"quiz_core/internal/models"
	"strconv"
	"strings"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

type createCardsRequest struct {
	Cards []createCardRequest `json:"cards" binding:"required"`
}

type createCardRequest struct {
	Question string   `json:"question" binding:"required"`
	Hint     string   `json:"hint" binding:"required"`
	Options  []string `json:"options" binding:"required"`
	Correct  []int    `json:"correct"`
}

type updateCardsRequest struct {
	Cards []updateCardRequest `json:"cards" binding:"required"`
}

type updateCardRequest struct {
	ID       uint      `json:"id" binding:"required"`
	Question *string   `json:"question"`
	Hint     *string   `json:"hint"`
	Options  *[]string `json:"options"`
	Correct  *[]int    `json:"correct"`
}

type cardsResponse struct {
	Cards []cardResponse `json:"cards"`
}

type cardResponse struct {
	ID       uint     `json:"id"`
	Question string   `json:"question"`
	Hint     string   `json:"hint"`
	Options  []string `json:"options"`
	Correct  []int    `json:"correct"`
}

func CreateCards(c *gin.Context) {
	packID, err := strconv.ParseUint(c.Param("pack_id"), 10, 0)

	if err != nil || packID == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid pack id"})
		return
	}

	var req createCardsRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "cards are required"})
		return
	}

	if len(req.Cards) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "cards are required"})
		return
	}

	if err := validateCreateCardsRequest(req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	authorID, ok := currentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	if err := ensurePackOwned(uint(packID), authorID); err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			c.JSON(http.StatusNotFound, gin.H{"error": "pack not found"})
			return
		}

		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create cards"})
		return
	}

	createdCards := make([]models.Card, 0, len(req.Cards))

	err = db.DB.Transaction(func(tx *gorm.DB) error {
		for _, cardReq := range req.Cards {
			card := models.Card{
				Question: strings.TrimSpace(cardReq.Question),
				Hint:     strings.TrimSpace(cardReq.Hint),
				PackID:   uint(packID),
			}

			if err := tx.Create(&card).Error; err != nil {
				return err
			}

			correct := correctIndexSet(cardReq.Correct)
			options := make([]models.CardOption, 0, len(cardReq.Options))
			for i, option := range cardReq.Options {
				options = append(options, models.CardOption{
					Content: strings.TrimSpace(option),
					IsRight: correct[i],
					CardID:  card.ID,
				})
			}

			if err := tx.Create(&options).Error; err != nil {
				return err
			}

			card.Options = options
			createdCards = append(createdCards, card)
		}

		return nil
	})

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create cards"})
		return
	}

	c.JSON(http.StatusOK, cardsResponse{Cards: cardResponses(createdCards)})
}

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

	response := cardsResponse{
		Cards: cardResponses(cards),
	}

	c.JSON(http.StatusOK, response)
}

func UpdateCards(c *gin.Context) {
	var req updateCardsRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "cards are required"})
		return
	}

	if len(req.Cards) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "cards are required"})
		return
	}

	if err := validateUpdateCardsRequest(req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	authorID, ok := currentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	updatedCards := make([]models.Card, 0, len(req.Cards))
	err := db.DB.Transaction(func(tx *gorm.DB) error {
		for _, cardReq := range req.Cards {
			card, err := findOwnedCard(tx, cardReq.ID, authorID)
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
				options, err := replaceCardOptions(tx, card.ID, *cardReq.Options, cardReq.Correct)
				if err != nil {
					return err
				}
				card.Options = options
			} else if cardReq.Correct != nil {
				options, err := updateCardCorrectOptions(tx, card.ID, *cardReq.Correct)
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

		if errors.Is(err, errCorrectOptionIndexOutOfRange) {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}

		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to update cards"})
		return
	}

	c.JSON(http.StatusOK, cardsResponse{Cards: cardResponses(updatedCards)})
}

func DeleteCard(c *gin.Context) {
	cardID, err := strconv.ParseUint(c.Param("card_id"), 10, 0)
	
	if err != nil || cardID == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid card id"})
		return
	}

	authorID, ok := currentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	err = db.DB.Transaction(func(tx *gorm.DB) error {
		card, err := findOwnedCard(tx, uint(cardID), authorID)
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

func cardResponses(cards []models.Card) []cardResponse {
	response := make([]cardResponse, 0, len(cards))
	for _, card := range cards {
		cardResp := cardResponse{
			ID:       card.ID,
			Question: card.Question,
			Hint:     card.Hint,
			Options:  make([]string, 0, len(card.Options)),
			Correct:  make([]int, 0),
		}

		for i, option := range card.Options {
			cardResp.Options = append(cardResp.Options, option.Content)
			if option.IsRight {
				cardResp.Correct = append(cardResp.Correct, i)
			}
		}

		response = append(response, cardResp)
	}

	return response
}

func validateCreateCardsRequest(req createCardsRequest) error {
	for _, card := range req.Cards {
		if strings.TrimSpace(card.Question) == "" {
			return errors.New("card question is required")
		}

		if strings.TrimSpace(card.Hint) == "" {
			return errors.New("card hint is required")
		}

		if len(card.Options) == 0 {
			return errors.New("card options are required")
		}

		for _, option := range card.Options {
			if strings.TrimSpace(option) == "" {
				return errors.New("card option content is required")
			}
		}

		for _, index := range card.Correct {
			if index < 0 || index >= len(card.Options) {
				return errors.New("correct option index is out of range")
			}
		}
	}

	return nil
}

func validateUpdateCardsRequest(req updateCardsRequest) error {
	for _, card := range req.Cards {
		if card.ID == 0 {
			return errors.New("card id is required")
		}

		if card.Question != nil && strings.TrimSpace(*card.Question) == "" {
			return errors.New("card question is required")
		}

		if card.Hint != nil && strings.TrimSpace(*card.Hint) == "" {
			return errors.New("card hint is required")
		}

		if card.Options != nil {
			if len(*card.Options) == 0 {
				return errors.New("card options are required")
			}

			for _, option := range *card.Options {
				if strings.TrimSpace(option) == "" {
					return errors.New("card option content is required")
				}
			}

			if card.Correct != nil {
				if err := validateCorrectIndexes(*card.Correct, len(*card.Options)); err != nil {
					return err
				}
			}
		}
	}

	return nil
}

func correctIndexSet(indexes []int) map[int]bool {
	result := make(map[int]bool, len(indexes))

	for _, index := range indexes {
		result[index] = true
	}

	return result
}

var errCorrectOptionIndexOutOfRange = errors.New("correct option index is out of range")

func validateCorrectIndexes(indexes []int, optionsCount int) error {
	for _, index := range indexes {
		if index < 0 || index >= optionsCount {
			return errCorrectOptionIndexOutOfRange
		}
	}

	return nil
}

func ensurePackOwned(packID uint, authorID uint) error {
	var pack models.Pack
	return db.DB.
		Where("id = ? AND author_id = ?", packID, authorID).
		First(&pack).Error
}

func findOwnedCard(tx *gorm.DB, cardID uint, authorID uint) (models.Card, error) {
	var card models.Card
	err := tx.
		Joins("JOIN packs ON packs.id = cards.pack_id").
		Where("cards.id = ? AND packs.author_id = ?", cardID, authorID).
		First(&card).Error
	return card, err
}

func replaceCardOptions(tx *gorm.DB, cardID uint, optionContents []string, correctIndexes *[]int) ([]models.CardOption, error) {
	correct := map[int]bool{}
	if correctIndexes != nil {
		correct = correctIndexSet(*correctIndexes)
	}

	if err := tx.Where("card_id = ?", cardID).Delete(&models.CardOption{}).Error; err != nil {
		return nil, err
	}

	options := make([]models.CardOption, 0, len(optionContents))
	for i, option := range optionContents {
		options = append(options, models.CardOption{
			Content: strings.TrimSpace(option),
			IsRight: correct[i],
			CardID:  cardID,
		})
	}

	if err := tx.Create(&options).Error; err != nil {
		return nil, err
	}

	return options, nil
}

func updateCardCorrectOptions(tx *gorm.DB, cardID uint, correctIndexes []int) ([]models.CardOption, error) {
	var options []models.CardOption
	if err := tx.
		Where("card_id = ?", cardID).
		Order("id ASC").
		Find(&options).Error; err != nil {
		return nil, err
	}

	if err := validateCorrectIndexes(correctIndexes, len(options)); err != nil {
		return nil, err
	}

	correct := correctIndexSet(correctIndexes)
	for i := range options {
		options[i].IsRight = correct[i]
		if err := tx.Save(&options[i]).Error; err != nil {
			return nil, err
		}
	}

	return options, nil
}
