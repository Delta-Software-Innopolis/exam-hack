package api

import (
	"net/http"
	"quiz_core/internal/db"
	"quiz_core/internal/models"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

type createPackRequest struct {
	Name string `json:"name" binding:"required"`
}

type packResponse struct {
	ID           uint         `json:"id"`
	Name         string       `json:"name"`
	CreationDate time.Time    `json:"creation_date"`
	UpdatingDate *time.Time   `json:"updating_date,omitempty"`
	Author       userResponse `json:"author"`
}

type packsResponse struct {
	Packs []packWithCardsResponse `json:"packs"`
}

type packWithCardsResponse struct {
	ID           uint           `json:"id"`
	Name         string         `json:"name"`
	CreationDate time.Time      `json:"creation_date"`
	UpdatingDate *time.Time     `json:"updating_date,omitempty"`
	Author       userResponse   `json:"author"`
	Cards        []cardResponse `json:"cards"`
}

type userResponse struct {
	ID   uint   `json:"id"`
	Name string `json:"name"`
}

func CreatePack(c *gin.Context) {
	var req createPackRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "pack name is required"})
		return
	}

	name, ok := packName(c, req.Name)
	if !ok {
		return
	}

	authorID, ok := currentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	pack := models.Pack{
		Name:         name,
		CreationDate: time.Now(),
		AuthorID:     authorID,
	}

	if err := db.DB.Create(&pack).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create pack"})
		return
	}

	c.JSON(http.StatusCreated, packResponse{
		ID:           pack.ID,
		Name:         pack.Name,
		CreationDate: pack.CreationDate,
		UpdatingDate: pack.UpdatingDate,
		Author:       currentUserResponse(c, authorID),
	})
}

func UpdatePack(c *gin.Context) {
	packID, err := strconv.ParseUint(c.Param("pack_id"), 10, 0)
	if err != nil || packID == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid pack id"})
		return
	}

	var req createPackRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "pack name is required"})
		return
	}

	name, ok := packName(c, req.Name)
	if !ok {
		return
	}

	authorID, ok := currentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	updatingDate := time.Now()
	var pack models.Pack
	err = db.DB.
		Where("id = ? AND author_id = ?", packID, authorID).
		First(&pack).Error
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "pack not found"})
			return
		}

		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to update pack"})
		return
	}

	pack.Name = name
	pack.UpdatingDate = &updatingDate

	if err := db.DB.Save(&pack).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to update pack"})
		return
	}

	c.JSON(http.StatusOK, packResponse{
		ID:           pack.ID,
		Name:         pack.Name,
		CreationDate: pack.CreationDate,
		UpdatingDate: pack.UpdatingDate,
		Author:       currentUserResponse(c, pack.AuthorID),
	})
}

func DeletePack(c *gin.Context) {
	packID, err := strconv.ParseUint(c.Param("pack_id"), 10, 0)
	if err != nil || packID == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid pack id"})
		return
	}

	authorID, ok := currentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	err = db.DB.Transaction(func(tx *gorm.DB) error {
		var pack models.Pack
		if err := tx.
			Where("id = ? AND author_id = ?", packID, authorID).
			First(&pack).Error; err != nil {
			return err
		}

		if err := tx.Exec(
			`DELETE FROM replies
			WHERE initial_id IN (SELECT id FROM comments WHERE pack_id = ?)
			OR reply_id IN (SELECT id FROM comments WHERE pack_id = ?)`,
			packID,
			packID,
		).Error; err != nil {
			return err
		}

		if err := tx.Exec("DELETE FROM comments WHERE pack_id = ?", packID).Error; err != nil {
			return err
		}

		if err := tx.Where("fork_id = ? OR original_id = ?", packID, packID).Delete(&models.Fork{}).Error; err != nil {
			return err
		}

		if err := tx.Exec("DELETE FROM published_pack WHERE id = ?", packID).Error; err != nil {
			return err
		}

		if err := tx.
			Where("card_id IN (?)", tx.Table("cards").Select("id").Where("pack_id = ?", packID)).
			Delete(&models.CardOption{}).Error; err != nil {
			return err
		}

		if err := tx.Where("pack_id = ?", packID).Delete(&models.Card{}).Error; err != nil {
			return err
		}

		return tx.Delete(&pack).Error
	})
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "pack not found"})
			return
		}

		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to delete pack"})
		return
	}

	c.Status(http.StatusOK)
}

func GetPacks(c *gin.Context) {
	authorID, ok := currentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	var packs []models.Pack

	if err := db.DB.
		Preload("Cards", func(tx *gorm.DB) *gorm.DB {
			return tx.Order("id ASC")
		}).
		Preload("Cards.Options", func(tx *gorm.DB) *gorm.DB {
			return tx.Order("id ASC")
		}).
		Where("author_id = ?", authorID).
		Order("id ASC").
		Find(&packs).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to get packs"})

		return
	}

	response := packsResponse{
		Packs: make([]packWithCardsResponse, 0, len(packs)),
	}

	for _, pack := range packs {
		response.Packs = append(response.Packs, packWithCardsResponse{
			ID:           pack.ID,
			Name:         pack.Name,
			CreationDate: pack.CreationDate,
			UpdatingDate: pack.UpdatingDate,
			Author:       currentUserResponse(c, authorID),
			Cards:        cardResponses(pack.Cards),
		})
	}

	c.JSON(http.StatusOK, response)
}

func packName(c *gin.Context, value string) (string, bool) {
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

func currentUserID(c *gin.Context) (uint, bool) {
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

func currentUserResponse(c *gin.Context, userID uint) userResponse {
	username, ok := c.Get("username")
	if !ok {
		return userResponse{ID: userID}
	}

	name, ok := username.(string)
	if !ok {
		return userResponse{ID: userID}
	}

	return userResponse{
		ID:   userID,
		Name: name,
	}
}
