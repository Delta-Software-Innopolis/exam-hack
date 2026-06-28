package pack_handlers

import (
	"net/http"

	sc "quiz_core/internal/api/shortcuts"
	structs "quiz_core/internal/api/structures"
	"quiz_core/internal/db"
	"quiz_core/internal/models"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func GetPacks(c *gin.Context) {
	authorID, ok := sc.CurrentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	var packs []models.Pack

	if err := db.DB.
		Joins("JOIN pack_permissions ON pack_permissions.pack_id = packs.id").
		Preload("Author").
		Preload("Cards", func(tx *gorm.DB) *gorm.DB {
			return tx.Order("id ASC")
		}).
		Preload("Cards.Options", func(tx *gorm.DB) *gorm.DB {
			return tx.Order("id ASC")
		}).
		Where("pack_permissions.user_id = ? AND pack_permissions.permission IN ?", authorID, []int{1, 2}).
		Order("id ASC").
		Find(&packs).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to get packs"})

		return
	}

	response := structs.PacksResponse{
		Packs: make([]structs.PackWithCardsResponse, 0, len(packs)),
	}

	for _, pack := range packs {
		response.Packs = append(response.Packs, sc.PackWithCardsResponse(pack))
	}

	c.JSON(http.StatusOK, response)
}
