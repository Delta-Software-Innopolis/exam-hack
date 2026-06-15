package api

import (
	"errors"
	"net/http"

	"auth/pkg/utils"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
)

type validateRequest struct {
	AccessToken string `json:"access_token" binding:"required"`
}

type validateResponse struct {
	Status string `json:"status"`
}

func Validate(c *gin.Context) {
	var req validateRequest

	if err := c.ShouldBindJSON(&req); err != nil || req.AccessToken == "" {
		c.JSON(http.StatusBadRequest, validateResponse{Status: "access token required"})
		return
	}

	if err := utils.ValidateAccessToken(req.AccessToken); err != nil {
		if errors.Is(err, jwt.ErrTokenExpired) {
			c.JSON(http.StatusOK, validateResponse{Status: "expired"})
			return
		}

		c.JSON(http.StatusOK, validateResponse{Status: "invalid"})
		return
	}

	c.JSON(http.StatusOK, validateResponse{Status: "ok"})
}
