package models

import "time"

type Token struct {
	ID               uint      `gorm:"column:id;primaryKey;autoIncrement;index:ix_tokens_id" json:"id"`
	UserID           uint      `gorm:"column:user_id;not null;index:ix_tokens_user_id" json:"user_id"`
	RefreshTokenHash string    `gorm:"column:refresh_token_hash;type:text;not null" json:"refresh_token_hash"`
	IsRevoked        bool      `gorm:"column:is_revoked;not null" json:"is_revoked"`
	ExpiresAt        time.Time `gorm:"column:expires_at;type:timestamptz;not null" json:"expires_at"`
}