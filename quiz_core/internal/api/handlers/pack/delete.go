package pack_handlers

import (
	"net/http"
	"strconv"

	sc "quiz_core/internal/api/shortcuts"
	"quiz_core/internal/db"
	"quiz_core/internal/models"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func DeletePack(c *gin.Context) {
	packID, err := strconv.ParseUint(c.Param("pack_id"), 10, 0)
	if err != nil || packID == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid pack id"})
		return
	}

	authorID, ok := sc.CurrentUserID(c)
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
