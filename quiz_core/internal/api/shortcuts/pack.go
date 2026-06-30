package api_shortcuts

import (
	"crypto/sha256"
	"encoding/hex"
	"net/http"
	"strconv"
	"strings"
	"time"

	structs "quiz_core/internal/api/structures"
	"quiz_core/internal/models"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
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

func CreatePackWithOwnerPermission(tx *gorm.DB, name string, authorID uint) (models.Pack, error) {
	// Needed for tests
	if tx.Dialector.Name() != "postgres" {
		return createPackWithOwnerPermissionPortable(tx, name, authorID)
	}

	var packID uint
	if err := tx.Raw("SELECT nextval(pg_get_serial_sequence('packs', 'id'))").Scan(&packID).Error; err != nil {
		return models.Pack{}, err
	}
	pack := models.Pack{
		ID:           packID,
		Name:         name,
		CreationDate: time.Now(),
		ShareCode:    packShareCode(packID),
		AuthorID:     authorID,
	}

	if err := tx.Create(&pack).Error; err != nil {
		return models.Pack{}, err
	}

	permission := models.PackPermission{
		UserID:     authorID,
		PackID:     pack.ID,
		Permission: 1,
	}

	if err := tx.Create(&permission).Error; err != nil {
		return models.Pack{}, err
	}

	return pack, nil
}

// Needed for tests
func createPackWithOwnerPermissionPortable(tx *gorm.DB, name string, authorID uint) (models.Pack, error) {
	pack := models.Pack{
		Name:         name,
		CreationDate: time.Now(),
		ShareCode:    "pending",
		AuthorID:     authorID,
	}

	if err := tx.Create(&pack).Error; err != nil {
		return models.Pack{}, err
	}

	pack.ShareCode = packShareCode(pack.ID)
	if err := tx.Model(&pack).Update("share_code", pack.ShareCode).Error; err != nil {
		return models.Pack{}, err
	}

	permission := models.PackPermission{
		UserID:     authorID,
		PackID:     pack.ID,
		Permission: 1,
	}

	if err := tx.Create(&permission).Error; err != nil {
		return models.Pack{}, err
	}

	return pack, nil
}

func PackWithCardsResponse(pack models.Pack) structs.PackWithCardsResponse {
	return structs.PackWithCardsResponse{
		ID:           pack.ID,
		Name:         pack.Name,
		CreationDate: pack.CreationDate,
		UpdatingDate: pack.UpdatingDate,
		ShareCode:    pack.ShareCode,
		Author: structs.UserResponse{
			ID:   pack.Author.ID,
			Name: pack.Author.Name,
		},
		Cards: CardResponses(pack.Cards),
	}
}

func packShareCode(packID uint) string {
	hash := sha256.Sum256([]byte(strconv.FormatUint(uint64(packID), 10)))
	return hex.EncodeToString(hash[:])
}
