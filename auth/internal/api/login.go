package api

import (
	"errors"
	"net/http"
	"strings"
	"time"

	"auth/internal/database"
	"auth/internal/models"
	"auth/pkg/utils"

	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
)

type LoginRequest struct {
	Username string `json:"username" binding:"required"`
	Password string `json:"password" binding:"required"`
}

type loginResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
}

func Login(c *gin.Context) {
	var req LoginRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "both username and password are required"})
		return
	}

	req.Username = strings.TrimSpace(req.Username)
	if req.Username == "" || req.Password == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "both username and password are required"})
		return
	}

	var user models.User
	if err := database.DB.Where("name = ?", req.Username).First(&user).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid username or password"})
			return
		}

		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to load user during login", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to login"})

		return
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PassHash), []byte(req.Password)); err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid username or password"})

		return
	}

	accessToken, refreshToken, err := utils.GenerateTokens(int(user.ID), user.Name)

	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to generate tokens during login", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to login"})
		
		return
	}

	token := models.Token{
		UserID:           user.ID,
		RefreshTokenHash: utils.HashToken(refreshToken),
		IsRevoked:        false,
		ExpiresAt:        time.Now().Add(utils.RefreshTokenTTL),
	}

	if err := database.DB.Create(&token).Error; err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to persist refresh token during login", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to login"})

		return
	}

	c.JSON(http.StatusOK, loginResponse{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	})
}
