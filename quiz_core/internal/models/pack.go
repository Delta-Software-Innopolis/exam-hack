package models

import "time"

type Pack struct {
	ID           uint       `gorm:"column:id;primaryKey;autoIncrement" json:"id"`
	Name         string     `gorm:"column:name;type:varchar(50);not null;index:ix_packs_name" json:"name"`
	Description  *string    `gorm:"column:description;type:text" json:"description,omitempty"`
	CreationDate time.Time  `gorm:"column:creation_date;type:timestamp with time zone;not null" json:"creation_date"`
	UpdatingDate *time.Time `gorm:"column:updating_date;type:timestamp with time zone" json:"updating_date,omitempty"`
	ShareCode    string     `gorm:"column:share_code;type:varchar(64);not null;uniqueIndex:ix_packs_share_code" json:"share_code"`
	AuthorID     uint       `gorm:"column:author_id;not null;index:ix_packs_author_id" json:"-"`

	Author      User             `gorm:"foreignKey:AuthorID;references:ID" json:"author,omitempty"`
	Permissions []PackPermission `gorm:"foreignKey:PackID;references:ID;constraint:OnDelete:CASCADE" json:"permissions,omitempty"`
	Cards       []Card           `gorm:"foreignKey:PackID;references:ID;constraint:OnDelete:CASCADE" json:"cards,omitempty"`
	Forks       []Fork           `gorm:"foreignKey:ForkID;references:ID" json:"forks,omitempty"`
	OriginalFor []Fork           `gorm:"foreignKey:OriginalID;references:ID" json:"original_for,omitempty"`
}

func (Pack) TableName() string {
	return "packs"
}
