package models

type User struct {
	ID       uint   `gorm:"column:id;primaryKey;autoIncrement;index:ix_users_id" json:"id"`
	Name     string `gorm:"column:name;type:varchar(30);not null;index:ix_users_name" json:"name"`
	PassHash string `gorm:"column:pass_hash;type:text;not null;index:ix_users_pass_hash" json:"pass_hash"`
}
