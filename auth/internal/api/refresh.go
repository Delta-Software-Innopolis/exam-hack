package api

import (
	"errors"
	"net/http"
	"time"

	"auth/internal/config"
	"auth/internal/database"
	"auth/internal/models"
	"auth/pkg/utils"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
	"gorm.io/gorm/clause"
)

type refreshRequest struct {
	RefreshToken string `json:"refresh_token"`
}

type refreshResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
}

func Refresh(c *gin.Context) {
	var req refreshRequest

	_ = c.ShouldBindJSON(&req)
	if req.RefreshToken == "" {
		cookieToken, err := c.Cookie("refresh_token")
		if err == nil {
			req.RefreshToken = cookieToken
		}
	}

	if req.RefreshToken == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "refresh token is required"})
		return
	}

	userID, err := utils.ValidateRefreshToken(req.RefreshToken)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid refresh token"})
		return
	}

	refreshTokenHash := utils.HashToken(req.RefreshToken)
	var response refreshResponse

	err = database.DB.Transaction(func(tx *gorm.DB) error {
		var storedToken models.Token

		err := tx.Clauses(clause.Locking{Strength: "UPDATE"}).
			Where("refresh_token_hash = ?", refreshTokenHash).
			First(&storedToken).Error
		if err != nil {
			return err
		}

		if storedToken.UserID != uint(userID) || storedToken.IsRevoked || time.Now().After(storedToken.ExpiresAt) {
			return gorm.ErrRecordNotFound
		}

		var user models.User
		if err := tx.First(&user, storedToken.UserID).Error; err != nil {
			return err
		}

		accessToken, newRefreshToken, err := utils.GenerateTokens(int(user.ID), user.Name)
		if err != nil {
			return err
		}

		if err := tx.Model(&storedToken).Update("is_revoked", true).Error; err != nil {
			return err
		}

		newStoredToken := models.Token{
			UserID:           user.ID,
			RefreshTokenHash: utils.HashToken(newRefreshToken),
			IsRevoked:        false,
			ExpiresAt:        time.Now().Add(utils.RefreshTokenTTL),
		}

		if err := tx.Create(&newStoredToken).Error; err != nil {
			return err
		}

		response = refreshResponse{
			AccessToken:  accessToken,
			RefreshToken: newRefreshToken,
		}

		return nil
	})

	if errors.Is(err, gorm.ErrRecordNotFound) {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid refresh token"})
		return
	}

	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to refresh tokens", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to refresh tokens"})
		return
	}

	c.SetCookie(
		"refresh_token",
		response.RefreshToken,
		int(utils.RefreshTokenTTL.Seconds()),
		"/",
		"",
		config.AppConfig.IsHttps,
		true,
	)

	c.JSON(http.StatusOK, response)
}
