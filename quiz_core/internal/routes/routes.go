package routes

import (
	"errors"
	"net/http"
	"quiz_core/internal/api"
	"quiz_core/internal/config"
	"quiz_core/pkg/utils"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
)

type tokenClaims struct {
	UserID   int    `json:"user_id"`
	Username string `json:"username"`
	Type     string `json:"type"`
	jwt.RegisteredClaims
}

func AuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		tokenString, ok := utils.BearerToken(c.GetHeader("Authorization"))
		if !ok {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "authorization bearer token required"})
			return
		}

		claims := &tokenClaims{}
		token, err := jwt.ParseWithClaims(tokenString, claims, func(token *jwt.Token) (any, error) {

			if token.Method != jwt.SigningMethodRS256 {
				return nil, errors.New("unexpected signing method")
			}

			return config.AppConfig.AuthPublic, nil
		})

		if err != nil || !token.Valid || claims.Type != "access" || claims.UserID == 0 || claims.Username == "" {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "invalid token"})
			return
		}

		c.Set("user_id", claims.UserID)
		c.Set("username", claims.Username)
		c.Next()
	}
}

func Setup() *gin.Engine {
	router := gin.Default()

	corsConfig := cors.DefaultConfig()
	corsConfig.AllowOrigins = config.AppConfig.Routes.CORSAddresses
	corsConfig.AllowMethods = []string{"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"}
	corsConfig.AllowHeaders = []string{"Origin", "Content-Type", "Authorization"}
	corsConfig.AllowCredentials = true
	router.Use(cors.New(corsConfig))

	router.GET("/ping", api.Pong)

	{
		core := router.Group("/core")
		core.Use(AuthMiddleware())

		core.POST("/pack", api.CreatePack)
		core.PATCH("/pack/:pack_id", api.UpdatePack)
		core.DELETE("/pack/:pack_id", api.DeletePack)
		core.GET("/packs", api.GetPacks)
		core.POST("/cards/:pack_id", api.CreateCards)
		core.PATCH("/cards", api.UpdateCards)
		core.DELETE("/cards/:card_id", api.DeleteCard)
		core.GET("/cards/:pack_id", api.GetCards)
	}

	return router
}
