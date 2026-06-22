package api_shortcuts

import (
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
	structs "quiz_core/internal/api/structures"
)

func PackName(c *gin.Context, value string) (string, bool) {
	name := strings.TrimSpace(value)
	if name == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "pack name is required"})
		return "", false
	}

	if len(name) > 50 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "pack name must be at most 50 characters"})
		return "", false
	}

	return name, true
}

func CurrentUserID(c *gin.Context) (uint, bool) {
	userID, ok := c.Get("user_id")
	if !ok {
		return 0, false
	}

	switch value := userID.(type) {
	case int:
		if value <= 0 {
			return 0, false
		}
		return uint(value), true
	case uint:
		return value, value > 0
	default:
		return 0, false
	}
}

func CurrentUserResponse(c *gin.Context, userID uint) structs.UserResponse {
	username, ok := c.Get("username")
	if !ok {
		return structs.UserResponse{ID: userID}
	}

	name, ok := username.(string)
	if !ok {
		return structs.UserResponse{ID: userID}
	}

	return structs.UserResponse{
		ID:   userID,
		Name: name,
	}
}
