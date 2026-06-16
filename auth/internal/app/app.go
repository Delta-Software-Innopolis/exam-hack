package app

import (
	"auth/internal/config"
	"auth/internal/database"
	"auth/internal/routes"
)

func Run() {
	config.Load()
	router := routes.Setup()

	database.Connect();

	router.Run("0.0.0.0:8081")
}
