package routes

import (
	"quiz_core/internal/api"
	"quiz_core/internal/config"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
)


func Setup() *gin.Engine {
	router := gin.Default()

	corsConfig := cors.DefaultConfig()
	corsConfig.AllowOrigins = config.AppConfig.Routes.CORSAddresses
	corsConfig.AllowMethods = []string{"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"}
	corsConfig.AllowHeaders = []string{"Origin", "Content-Type", "Authorization"}
	corsConfig.AllowCredentials = true
	router.Use(cors.New(corsConfig))

	router.GET("/ping", api.Pong)

	return router
}
