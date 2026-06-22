package api_shortcuts

import (
	"errors"
	structs "quiz_core/internal/api/structures"
	"quiz_core/internal/models"
	"quiz_core/internal/db"
	"strings"

	"gorm.io/gorm"
)


func ValidateCreateCardsRequest(req structs.CreateCardsRequest) error {
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

func CardResponses(cards []models.Card) []structs.CardResponse {
	response := make([]structs.CardResponse, 0, len(cards))
	
	for _, card := range cards {
		cardResp := structs.CardResponse{
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

func ValidateUpdateCardsRequest(req structs.UpdateCardsRequest) error {
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
				if err := ValidateCorrectIndexes(*card.Correct, len(*card.Options)); err != nil {
					return err
				}
			}
		}
	}

	return nil
}

func CorrectIndexSet(indexes []int) map[int]bool {
	result := make(map[int]bool, len(indexes))

	for _, index := range indexes {
		result[index] = true
	}

	return result
}

var ErrCorrectOptionIndexOutOfRange = errors.New("correct option index is out of range")

func ValidateCorrectIndexes(indexes []int, optionsCount int) error {
	for _, index := range indexes {
		if index < 0 || index >= optionsCount {
			return ErrCorrectOptionIndexOutOfRange
		}
	}

	return nil
}

func EnsurePackOwned(packID uint, authorID uint) error {
	var pack models.Pack
	
	return db.DB.
		Where("id = ? AND author_id = ?", packID, authorID).
		First(&pack).Error
}

func FindOwnedCard(tx *gorm.DB, cardID uint, authorID uint) (models.Card, error) {
	var card models.Card
	err := tx.
		Joins("JOIN packs ON packs.id = cards.pack_id").
		Where("cards.id = ? AND packs.author_id = ?", cardID, authorID).
		First(&card).Error
	return card, err
}

func ReplaceCardOptions(tx *gorm.DB, cardID uint, optionContents []string, correctIndexes *[]int) ([]models.CardOption, error) {
	correct := map[int]bool{}
	if correctIndexes != nil {
		correct = CorrectIndexSet(*correctIndexes)
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

func UpdateCardCorrectOptions(tx *gorm.DB, cardID uint, correctIndexes []int) ([]models.CardOption, error) {
	var options []models.CardOption
	if err := tx.
		Where("card_id = ?", cardID).
		Order("id ASC").
		Find(&options).Error; err != nil {
		return nil, err
	}

	if err := ValidateCorrectIndexes(correctIndexes, len(options)); err != nil {
		return nil, err
	}

	correct := CorrectIndexSet(correctIndexes)
	for i := range options {
		options[i].IsRight = correct[i]
		if err := tx.Save(&options[i]).Error; err != nil {
			return nil, err
		}
	}

	return options, nil
}
