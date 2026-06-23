package llm

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"quiz_core/internal/config"
)

const (
	maxDocumentSize     = 1 * 1024 * 1024
	defaultCardCount    = 5
	maxCardCount        = 20
	llmRequestTimeout   = 120 * time.Second
	minTextLength       = 10
)

var httpClient = &http.Client{Timeout: llmRequestTimeout}

type generateCardsLLMRequest struct {
	Text     string `json:"text"`
	CardType string `json:"card_type"`
	Count    int    `json:"count"`
}

type Card struct {
	Type           string   `json:"type"`
	Question       string   `json:"question"`
	Options        []string `json:"options"`
	CorrectIndices []int    `json:"correct_indices"`
	CorrectAnswer  *string  `json:"correct_answer"`
	Hint           string   `json:"hint"`
	Explanation    string   `json:"explanation"`
}

type GenerateCardsResponse struct {
	Cards []Card `json:"cards"`
}

func GenerateCards(ctx *gin.Context) {
	ctx.Request.Body = http.MaxBytesReader(ctx.Writer, ctx.Request.Body, maxDocumentSize)
	if err := ctx.Request.ParseMultipartForm(maxDocumentSize); err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "failed to parse multipart form: " + err.Error()})
		return
	}

	fileHeader, err := ctx.FormFile("document")
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "document is required"})
		return
	}

	if !strings.HasSuffix(strings.ToLower(fileHeader.Filename), ".txt") {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "only .txt documents are supported"})
		return
	}

	file, err := fileHeader.Open()
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "failed to open uploaded document"})
		return
	}
	defer file.Close()

	textBytes, err := io.ReadAll(io.LimitReader(file, maxDocumentSize))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "failed to read uploaded document"})
		return
	}

	text := strings.TrimSpace(string(textBytes))
	if len(text) == 0 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "document is empty"})
		return
	}
	if len(text) < minTextLength {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": fmt.Sprintf("document must be at least %d characters", minTextLength)})
		return
	}

	cardType := ctx.PostForm("card_type")
	if cardType != "multiple_choice" && cardType != "single_answer" {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "card_type must be 'multiple_choice' or 'single_answer'"})
		return
	}

	count := defaultCardCount
	if countStr := ctx.PostForm("count"); countStr != "" {
		parsedCount, err := strconv.Atoi(countStr)
		if err != nil || parsedCount < 1 || parsedCount > maxCardCount {
			ctx.JSON(http.StatusBadRequest, gin.H{"error": fmt.Sprintf("count must be an integer between 1 and %d", maxCardCount)})
			return
		}
		count = parsedCount
	}

	llmReq := generateCardsLLMRequest{
		Text:     text,
		CardType: cardType,
		Count:    count,
	}
	llmReqBody, err := json.Marshal(llmReq)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "failed to build LLM request"})
		return
	}

	llmURL := strings.TrimSuffix(config.AppConfig.LLMService.URL, "/") + "/generate"
	req, err := http.NewRequestWithContext(ctx.Request.Context(), http.MethodPost, llmURL, bytes.NewReader(llmReqBody))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "failed to create LLM request"})
		return
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := httpClient.Do(req)
	if err != nil {
		log.Printf("LLM request failed: %v", err)
		ctx.JSON(http.StatusBadGateway, gin.H{"error": "failed to reach LLM service: " + err.Error()})
		return
	}
	defer resp.Body.Close()

	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		log.Printf("LLM response read failed: %v", err)
		ctx.JSON(http.StatusBadGateway, gin.H{"error": "failed to read LLM response"})
		return
	}

	if resp.StatusCode != http.StatusOK {
		log.Printf("LLM returned %d: %s", resp.StatusCode, string(respBody))
		ctx.JSON(http.StatusBadGateway, gin.H{"error": "LLM service returned error", "details": string(respBody)})
		return
	}

	var llmResp GenerateCardsResponse
	if err := json.Unmarshal(respBody, &llmResp); err != nil {
		log.Printf("LLM response parse failed: %v, body: %s", err, string(respBody))
		ctx.JSON(http.StatusBadGateway, gin.H{"error": "failed to parse LLM response"})
		return
	}

	ctx.JSON(http.StatusOK, llmResp)
}
