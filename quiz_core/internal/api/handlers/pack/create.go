package pack_handlers

import (
	"net/http"
	"time"

	sc "quiz_core/internal/api/shortcuts"
	structs "quiz_core/internal/api/structures"
	"quiz_core/internal/db"
	"quiz_core/internal/models"

	"github.com/gin-gonic/gin"
)


func CreatePack(c *gin.Context) {
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

	pack := models.Pack{
		Name:         name,
		CreationDate: time.Now(),
		AuthorID:     authorID,
	}

	if err := db.DB.Create(&pack).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create pack"})
		return
	}

	c.JSON(http.StatusCreated, structs.PackResponse{
		ID:           pack.ID,
		Name:         pack.Name,
		CreationDate: pack.CreationDate,
		UpdatingDate: pack.UpdatingDate,
		Author:       sc.CurrentUserResponse(c, authorID),
	})
}