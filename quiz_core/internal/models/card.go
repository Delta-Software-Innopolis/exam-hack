package models

type Card struct {
	ID       uint   `gorm:"column:id;primaryKey;autoIncrement" json:"id"`
	Question string `gorm:"column:question;type:text;not null" json:"question"`
	PackID   uint   `gorm:"column:pack_id;not null;index:ix_cards_pack_id" json:"pack_id"`
	Hint     string `gorm:"column:hint;type:text;not null" json:"hint"`

	Pack    Pack         `gorm:"foreignKey:PackID;references:ID" json:"pack,omitempty"`
	Options []CardOption `gorm:"foreignKey:CardID;references:ID" json:"options,omitempty"`
}

func (Card) TableName() string {
	return "cards"
}
