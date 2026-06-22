package pack_handlers

import (
	"net/http"
	"strconv"
	"time"

	sc "quiz_core/internal/api/shortcuts"
	structs "quiz_core/internal/api/structures"
	"quiz_core/internal/db"
	"quiz_core/internal/models"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func UpdatePack(c *gin.Context) {
	packID, err := strconv.ParseUint(c.Param("pack_id"), 10, 0)
	if err != nil || packID == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid pack id"})
		return
	}

	var req structs.CreatePackRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "pack name is required"})
		return
	}

	name, ok := sc.PackName(c, req.Name)
	if !ok {
		return
	}

	authorID, ok := sc.CurrentUserID(c)
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

	c.JSON(http.StatusOK, structs.PackResponse{
		ID:           pack.ID,
		Name:         pack.Name,
		CreationDate: pack.CreationDate,
		UpdatingDate: pack.UpdatingDate,
		Author:       sc.CurrentUserResponse(c, pack.AuthorID),
	})
}