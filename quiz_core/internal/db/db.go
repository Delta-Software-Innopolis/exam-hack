package db

import (
	"log"
	"quiz_core/internal/config"

	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)


var DB *gorm.DB;

func Connect() {
	dsn := "host=" + config.AppConfig.Database.Host + " user=" + config.AppConfig.Database.User + " password=" + config.AppConfig.Database.Password + " dbname=" + config.AppConfig.Database.Name + " sslmode=disable TimeZone=Europe/Moscow"

	var err error
	DB, err = gorm.Open(postgres.Open(dsn), &gorm.Config{})

	if err != nil {
		log.Fatalf("Error while connecting to database: %s\n", err.Error())
	}
}
