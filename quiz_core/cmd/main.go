package main

import (
	"quiz_core/internal/config"
	"quiz_core/internal/db"
	"quiz_core/internal/routes"
)

func main() {
	config.Load()
	db.Connect()

	router := routes.Setup()

	router.Run(config.AppConfig.Host)
}