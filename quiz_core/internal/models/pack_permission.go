package models

type PackPermission struct {
	UserID     uint `gorm:"column:user_id;primaryKey;index:ix_pack_permissions_user_id" json:"user_id"`
	PackID     uint `gorm:"column:pack_id;primaryKey;index:ix_pack_permissions_pack_id" json:"pack_id"`
	Permission int  `gorm:"column:permission;not null;index:ix_pack_permissions_permission" json:"permission"`

	User User `gorm:"foreignKey:UserID;references:ID" json:"user,omitempty"`
	Pack Pack `gorm:"foreignKey:PackID;references:ID" json:"pack,omitempty"`
}

func (PackPermission) TableName() string {
	return "pack_permissions"
}
