package api_structures

import "time"

type CreatePackRequest struct {
	Name string `json:"name" binding:"required"`
	Cards []CreateCardRequest `json:"cards"`
}

type PackResponse struct {
	ID           uint         `json:"id"`
	Name         string       `json:"name"`
	CreationDate time.Time    `json:"creation_date"`
	UpdatingDate *time.Time   `json:"updating_date,omitempty"`
	ShareCode    string       `json:"share_code"`
	Author       UserResponse `json:"author"`
}

type PacksResponse struct {
	Packs []PackWithCardsResponse `json:"packs"`
}

type PackWithCardsResponse struct {
	ID           uint           `json:"id"`
	Name         string         `json:"name"`
	CreationDate time.Time      `json:"creation_date"`
	UpdatingDate *time.Time     `json:"updating_date,omitempty"`
	ShareCode    string         `json:"share_code"`
	Author       UserResponse   `json:"author"`
	Cards        []CardResponse `json:"cards"`
}

type UserResponse struct {
	ID   uint   `json:"id"`
	Name string `json:"name"`
}
