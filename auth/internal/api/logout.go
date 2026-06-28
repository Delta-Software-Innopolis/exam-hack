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

type logoutRequest struct {
	RefreshToken string `json:"refresh_token"`
}

type logoutResponse struct {
	Status string `json:"status"`
}

func Logout(c *gin.Context) {
	var req logoutRequest

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

	err = database.DB.Transaction(func(tx *gorm.DB) error {
		var storedToken models.Token

		err := tx.Clauses(clause.Locking{Strength: "UPDATE"}).
			Where("refresh_token_hash = ?", refreshTokenHash).
			First(&storedToken).Error
		if err != nil {
			return err
		}

		if storedToken.UserID != uint(userID) || time.Now().After(storedToken.ExpiresAt) {
			return gorm.ErrRecordNotFound
		}

		if storedToken.IsRevoked {
			return nil
		}

		return tx.Model(&storedToken).Update("is_revoked", true).Error
	})

	if errors.Is(err, gorm.ErrRecordNotFound) {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid refresh token"})
		return
	}

	if err != nil {
		utils.LogEndpointError(c, http.StatusInternalServerError, "failed to logout", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to logout"})
		return
	}

	c.SetCookie(
		"refresh_token",
		"",
		-1,
		"/",
		"",
		config.AppConfig.IsHttps,
		true,
	)

	c.JSON(http.StatusOK, logoutResponse{Status: "ok"})
}
