package models

type Fork struct {
	ForkID     uint `gorm:"column:fork_id;primaryKey;index:ix_forks_fork_id" json:"fork_id"`
	OriginalID uint `gorm:"column:original_id;primaryKey;index:ix_forks_original_id" json:"original_id"`

	Fork     Pack `gorm:"foreignKey:ForkID;references:ID" json:"fork,omitempty"`
	Original Pack `gorm:"foreignKey:OriginalID;references:ID" json:"original,omitempty"`
}

func (Fork) TableName() string {
	return "forks"
}
