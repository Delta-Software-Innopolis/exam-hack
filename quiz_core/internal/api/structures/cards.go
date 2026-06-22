package api_structures

type CreateCardsRequest struct {
	Cards []CreateCardRequest `json:"cards" binding:"required"`
}

type CreateCardRequest struct {
	Question string   `json:"question" binding:"required"`
	Hint     string   `json:"hint" binding:"required"`
	Options  []string `json:"options" binding:"required"`
	Correct  []int    `json:"correct"`
}

type CardsResponse struct {
	Cards []CardResponse `json:"cards"`
}

type CardResponse struct {
	ID       uint     `json:"id"`
	Question string   `json:"question"`
	Hint     string   `json:"hint"`
	Options  []string `json:"options"`
	Correct  []int    `json:"correct"`
}

type UpdateCardsRequest struct {
	Cards []UpdateCardRequest `json:"cards" binding:"required"`
}

type UpdateCardRequest struct {
	ID       uint      `json:"id" binding:"required"`
	Question *string   `json:"question"`
	Hint     *string   `json:"hint"`
	Options  *[]string `json:"options"`
	Correct  *[]int    `json:"correct"`
}