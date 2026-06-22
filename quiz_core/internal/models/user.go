package models

type User struct {
	ID       uint   `gorm:"column:id;primaryKey;autoIncrement" json:"id"`
	Name     string `gorm:"column:name;type:varchar(30);not null;index:ix_users_name" json:"name"`
	PassHash string `gorm:"column:pass_hash;type:text;not null;index:ix_users_pass_hash" json:"-"`

	Packs []Pack `gorm:"foreignKey:AuthorID;references:ID" json:"packs,omitempty"`
}

func (User) TableName() string {
	return "users"
}
