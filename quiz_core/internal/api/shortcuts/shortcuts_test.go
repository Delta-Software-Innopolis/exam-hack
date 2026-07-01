package api_shortcuts

import (
	"net/http/httptest"
	structures "quiz_core/internal/api/structures"
	"quiz_core/internal/models"
	"testing"

	"github.com/gin-gonic/gin"
)

type Test struct {
	name          string
	performAction func(*shortcutTestState, string)
	verifyResult  func(*testing.T, *shortcutTestState, string)
}

type shortcutTestState struct {
	ctx          *gin.Context
	cardResponse []structures.CardResponse
	correctSet   map[int]bool
	err          error
	name         string
	ok           bool
	userID       uint
	userResponse structures.UserResponse
	packResponse structures.PackWithCardsResponse
}

func newShortcutTestState() *shortcutTestState {
	gin.SetMode(gin.TestMode)
	recorder := httptest.NewRecorder()
	ctx, _ := gin.CreateTestContext(recorder)
	return &shortcutTestState{ctx: ctx}
}

func Test_Shortcuts(t *testing.T) {
	testData := map[string][]string{
		"pack name trims value":          {"  Biology  "},
		"pack name rejects empty":        {"   "},
		"current user id reads context":  {"7"},
		"validate create cards request":  {"valid"},
		"validate update cards request":  {"invalid"},
		"card responses map correctness": {"cards"},
		"pack response maps author":      {"pack"},
	}

	expectedData := map[string][]string{
		"pack name trims value":          {"Biology"},
		"pack name rejects empty":        {"false"},
		"current user id reads context":  {"7"},
		"validate create cards request":  {"nil"},
		"validate update cards request":  {"error"},
		"card responses map correctness": {"2"},
		"pack response maps author":      {"Author"},
	}

	tests := []Test{
		{
			name: "pack name trims value",
			performAction: func(p *shortcutTestState, testName string) {
				p.name, p.ok = PackName(p.ctx, testData[testName][0])
			},
			verifyResult: func(t *testing.T, p *shortcutTestState, testName string) {
				if !p.ok {
					t.Fatal("expected pack name to be accepted")
				}
				if p.name != expectedData[testName][0] {
					t.Fatalf("expected name %q, got %q", expectedData[testName][0], p.name)
				}
			},
		},
		{
			name: "pack name rejects empty",
			performAction: func(p *shortcutTestState, testName string) {
				_, p.ok = PackName(p.ctx, testData[testName][0])
			},
			verifyResult: func(t *testing.T, p *shortcutTestState, testName string) {
				if p.ok {
					t.Fatal("expected empty pack name to be rejected")
				}
			},
		},
		{
			name: "current user id reads context",
			performAction: func(p *shortcutTestState, testName string) {
				p.ctx.Set("user_id", 7)
				p.ctx.Set("username", "alice")
				p.userID, p.ok = CurrentUserID(p.ctx)
				p.userResponse = CurrentUserResponse(p.ctx, p.userID)
			},
			verifyResult: func(t *testing.T, p *shortcutTestState, testName string) {
				if !p.ok || p.userID != 7 {
					t.Fatalf("expected user id 7, got %d ok=%v", p.userID, p.ok)
				}
				if p.userResponse.Name != "alice" {
					t.Fatalf("expected username alice, got %q", p.userResponse.Name)
				}
			},
		},
		{
			name: "validate create cards request",
			performAction: func(p *shortcutTestState, testName string) {
				p.err = ValidateCreateCardsRequest(structures.CreateCardsRequest{
					Cards: []structures.CreateCardRequest{
						{
							Question: "Question",
							Hint:     "Hint",
							Options:  []string{"a", "b"},
							Correct:  []int{1},
						},
					},
				})
			},
			verifyResult: func(t *testing.T, p *shortcutTestState, testName string) {
				if p.err != nil {
					t.Fatalf("expected valid request, got %v", p.err)
				}
			},
		},
		{
			name: "validate update cards request",
			performAction: func(p *shortcutTestState, testName string) {
				question := "   "
				p.err = ValidateUpdateCardsRequest(structures.UpdateCardsRequest{
					Cards: []structures.UpdateCardRequest{
						{ID: 1, Question: &question},
					},
				})
			},
			verifyResult: func(t *testing.T, p *shortcutTestState, testName string) {
				if p.err == nil {
					t.Fatal("expected validation error")
				}
			},
		},
		{
			name: "card responses map correctness",
			performAction: func(p *shortcutTestState, testName string) {
				p.correctSet = CorrectIndexSet([]int{0, 2})
				p.cardResponse = CardResponses([]models.Card{
					{
						ID:       10,
						Question: "Question",
						Hint:     "Hint",
						Options: []models.CardOption{
							{Content: "a", IsRight: true},
							{Content: "b", IsRight: false},
							{Content: "c", IsRight: true},
						},
					},
				})
			},
			verifyResult: func(t *testing.T, p *shortcutTestState, testName string) {
				if !p.correctSet[0] || !p.correctSet[2] || p.correctSet[1] {
					t.Fatalf("unexpected correct set: %#v", p.correctSet)
				}
				if len(p.cardResponse) != 1 || len(p.cardResponse[0].Correct) != 2 {
					t.Fatalf("unexpected card response: %#v", p.cardResponse)
				}
			},
		},
		{
			name: "pack response maps author",
			performAction: func(p *shortcutTestState, testName string) {
				p.packResponse = PackWithCardsResponse(models.Pack{
					ID:        1,
					Name:      "Pack",
					ShareCode: "share",
					Author: models.User{
						ID:   2,
						Name: expectedData[testName][0],
					},
				})
			},
			verifyResult: func(t *testing.T, p *shortcutTestState, testName string) {
				if p.packResponse.Author.Name != expectedData[testName][0] {
					t.Fatalf("expected author %q, got %q", expectedData[testName][0], p.packResponse.Author.Name)
				}
			},
		},
	}

	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			p := newShortcutTestState()
			if test.performAction != nil {
				test.performAction(p, test.name)
			}
			test.verifyResult(t, p, test.name)
		})
	}
}
