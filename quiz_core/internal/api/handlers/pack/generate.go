package pack_handlers

import (
	"bytes"
	"encoding/json"
	"errors"
	"io"
	"log"
	"net/http"
	"os"
	"path/filepath"
	"strconv"
	"time"

	sc "quiz_core/internal/api/shortcuts"
	"quiz_core/internal/models"

	"code.sajari.com/docconv/v2"
	"github.com/gin-gonic/gin"
)

type GeneratePackRequest struct {
	Name  string `form:"name"      binding:"required"`
	Type  string `form:"card_type" binding:"required"`
	Count string `form:"count"     binding:"required"`
}

type LLMGenerateRequest struct {
	Text  string `json:"name"      binding:"required"`
	Type  string `json:"card_type" binding:"required"`
	Count int    `json:"count"     binding:"required"`
}

type LLMGenerateResponse struct {
	Cards []CardOut `json:"cards"`
}

type CardOut struct {
	Type           string   `json:"type"`
	Question       string   `json:"question"`
	Options        []string `json:"options,omitempty"`
	CorrectIndices []int    `json:"correct_indices,omitempty"`
	CorrectAnswer  string   `json:"correct_answer,omitempty"`
	Hint           string   `json:"hint"`
}

func extractTextFromFiles(paths []string) (string, error) {
	log.Println("extracting text...")
	text := ""
	for _, file := range paths {
		switch filepath.Ext(file) {
		case ".doc", ".docx", ".pdf", ".odt", ".pages", ".rtf", ".html", ".htm", ".xml", ".pptx":
			res, err := docconv.ConvertPath(file)
			if err != nil {
				return "", err
			}
			text += res.Body
		case ".txt", ".md":
			content, err := os.ReadFile(file)
			if err != nil {
				return "", err
			}
			text += string(content)
		default:
			return "", errors.New("Error while parsing files: file " + file + ": extension not supported")
		}
	}

	return text, nil
}

func GeneratePack(c *gin.Context) {
	var pack_req GeneratePackRequest
	if err := c.ShouldBind(&pack_req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	pack_name, ok := sc.PackName(c, pack_req.Name)
	if !ok {
		c.JSON(http.StatusBadRequest, gin.H{"error": "bad pack name"})
		return
	}

	authorID, ok := sc.CurrentUserID(c)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "invalid token user"})
		return
	}

	form, err := c.MultipartForm()
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": err.Error(),
		})
		return
	}

	files := form.File["files"]
	if len(files) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "no files uploaded"})
		return
	}

	var paths []string
	var savedFiles []*os.File

	for _, file := range files {
		tmpFile, err := os.CreateTemp("/tmp", "file-*"+filepath.Ext(file.Filename))
		if err != nil {
			cleanupFiles(savedFiles)
			c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create temp file: " + err.Error()})
			return
		}
		savedFiles = append(savedFiles, tmpFile)

		if err := c.SaveUploadedFile(file, tmpFile.Name()); err != nil {
			cleanupFiles(savedFiles)
			c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to save file: " + err.Error()})
			return
		}

		paths = append(paths, tmpFile.Name())
	}

	defer cleanupFiles(savedFiles)

	text, err := extractTextFromFiles(paths)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	log.Println("sending request to llm service...")

	reqBody := LLMGenerateRequest{Text: text, Type: pack_req.Type, Count: pack_req.Count}

	jsonData, err := json.Marshal(reqBody)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	client := &http.Client{
		Timeout: 120 * time.Second,
	}

	req, err := http.NewRequest(
		"POST",
		"http://llm-service:7000/generate",
		bytes.NewBuffer(jsonData),
	)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	req.Header.Set("Content-Type", "application/json")

	resp, err := client.Do(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	log.Println("req sent")
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	if resp.StatusCode != http.StatusOK {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	var generateResp LLMGenerateResponse
	if err := json.Unmarshal(body, &generateResp); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	
	log.Printf("-------------------PACK-----------------------")
	for i, card := range generateResp.Cards {
		log.Printf("CARD No%d:\n", i)
		log.Printf("Question: %s\n", card.Question)
		log.Printf("Hint: %s\n", card.Hint)
		log.Printf("Options:\n")
		correct := sc.CorrectIndexSet(card.CorrectIndices)
		for j, option := range card.Options {
			log.Printf("%d: %s ", j, option)
			if correct[j] {
				log.Printf("(CORRECT)\n")
			} else {
				log.Printf("(INCORRECT)\n")
			}
		}
		log.Printf("\n")
	}

	createdCards := make([]models.Card, 0, len(generateResp.Cards))
	for _, cardReq := range generateResp.Cards {
		card := models.Card{
			Question: cardReq.Question,
			Hint:     cardReq.Hint,
		}

		correct := sc.CorrectIndexSet(cardReq.CorrectIndices)
		options := make([]models.CardOption, 0, len(cardReq.Options))
		for i, option := range cardReq.Options {
			options = append(options, models.CardOption{
				Content: option,
				IsRight: correct[i],
			})
		}

		card.Options = options
		createdCards = append(createdCards, card)
	}


	pack := models.Pack{
		Name: pack_name,
		CreationDate: time.Now(),
		AuthorID: authorID,
		Cards: createdCards,
	}

	c.JSON(http.StatusOK, pack)
}

func cleanupFiles(files []*os.File) {
	for _, f := range files {
		if f != nil {
			f.Close()
			os.Remove(f.Name())
		}
	}
}
