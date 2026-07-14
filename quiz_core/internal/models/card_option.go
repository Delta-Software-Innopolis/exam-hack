package models

type CardOption struct {
	ID      uint   `gorm:"column:id;primaryKey;autoIncrement;index:ix_card_options_id" json:"id"`
	Content string `gorm:"column:content;type:text;not null" json:"content"`
	IsRight bool   `gorm:"column:is_right;not null" json:"is_right"`
	CardID  uint   `gorm:"column:card_id;not null" json:"card_id"`

	Card Card `gorm:"foreignKey:CardID;references:ID;constraint:OnDelete:CASCADE" json:"card,omitempty"`
}

func (CardOption) TableName() string {
	return "card_options"
}
