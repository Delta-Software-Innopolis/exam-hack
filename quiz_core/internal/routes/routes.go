package routes

import (
	"log"
	"errors"
	"net/http"
	pack "quiz_core/internal/api/handlers/pack"
	cards "quiz_core/internal/api/handlers/cards"
	ping "quiz_core/internal/api/handlers/ping"
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
	router.MaxMultipartMemory = 100 << 20 // 100 MB
	corsConfig := cors.DefaultConfig()
	corsConfig.AllowOrigins = config.AppConfig.Routes.CORSAddresses
	corsConfig.AllowMethods = []string{"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"}
	corsConfig.AllowHeaders = []string{"Origin", "Content-Type", "Authorization"}
	corsConfig.AllowCredentials = true
	router.Use(cors.New(corsConfig))

	router.GET("/ping", ping.Pong)
	{
		core := router.Group("/core")
		core.Use(AuthMiddleware())

		core.POST("/pack", pack.CreatePack)
		core.PATCH("/pack/:pack_id", pack.UpdatePack)
		core.DELETE("/pack/:pack_id", pack.DeletePack)
		core.GET("/packs", pack.GetPacks)
		router.POST("/pack/generate", pack.GeneratePack)
		
		core.POST("/cards/:pack_id", cards.CreateCards)
		core.PATCH("/cards", cards.UpdateCards)
		core.DELETE("/cards/:card_id", cards.DeleteCard)
		core.GET("/cards/:pack_id", cards.GetCards)
	}


	return router
}
