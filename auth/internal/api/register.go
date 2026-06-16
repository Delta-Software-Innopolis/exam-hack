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

type RegisterRequest struct {
	Username string `json:"username" binding:"required"`
	Password string `json:"password" binding:"required"`
}

type registerResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
}

func Register(c *gin.Context) {
	var req RegisterRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "both username and password are required"})
		return
	}

	req.Username = strings.TrimSpace(req.Username)
	if req.Username == "" || req.Password == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "both username and password are required"})
		return
	}

	if len(req.Username) > 30 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "username must be at most 30 characters"})
		return
	}

	passHash, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to hash password during registration", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to register user"})

		return
	}

	var response registerResponse

	err = database.DB.Transaction(func(tx *gorm.DB) error {
		var existingUser models.User
		err := tx.Where("name = ?", req.Username).First(&existingUser).Error

		if err == nil {
			return gorm.ErrDuplicatedKey
		}

		if !errors.Is(err, gorm.ErrRecordNotFound) {
			return err
		}

		user := models.User{
			Name:     req.Username,
			PassHash: string(passHash),
		}

		if err := tx.Create(&user).Error; err != nil {
			return err
		}

		accessToken, refreshToken, err := utils.GenerateTokens(int(user.ID), user.Name)

		if err != nil {
			return err
		}

		token := models.Token{
			UserID:           user.ID,
			RefreshTokenHash: utils.HashToken(refreshToken),
			IsRevoked:        false,
			ExpiresAt:        time.Now().Add(utils.RefreshTokenTTL),
		}
		
		if err := tx.Create(&token).Error; err != nil {
			return err
		}

		response = registerResponse{
			AccessToken:  accessToken,
			RefreshToken: refreshToken,
		}
		return nil
	})

	if errors.Is(err, gorm.ErrDuplicatedKey) {
		c.JSON(http.StatusConflict, gin.H{"error": "user already exists"})
		return
	}

	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to register user", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to register user"})
		return
	}

	c.JSON(http.StatusOK, response)
}
