package pack_handlers

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	cards "quiz_core/internal/api/handlers/cards"
	sc "quiz_core/internal/api/shortcuts"
	"quiz_core/internal/db"
	"quiz_core/internal/models"
	"strconv"
	"strings"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/glebarez/sqlite"
	"gorm.io/gorm"
)

type Test struct {
	name          string
	performAction func(*coreTestApp, string)
	verifyResult  func(*testing.T, *coreTestApp, string)
}

type coreTestApp struct {
	router *gin.Engine
	db     *gorm.DB
	t      *testing.T
	users  map[string]models.User
	packs  map[string]models.Pack
	cards  map[string]models.Card
}

type coreResponse struct {
	ID          uint                `json:"id"`
	Name        string              `json:"name"`
	Description *string             `json:"description"`
	ShareCode   string              `json:"share_code"`
	Author      userResponse        `json:"author"`
	Packs       []packWithCardsBody `json:"packs"`
	Cards       []cardBody          `json:"cards"`
	Error       string              `json:"error"`
}

type packWithCardsBody struct {
	ID          uint         `json:"id"`
	Name        string       `json:"name"`
	Description *string      `json:"description"`
	ShareCode   string       `json:"share_code"`
	Author      userResponse `json:"author"`
	Cards       []cardBody   `json:"cards"`
}

type userResponse struct {
	ID   uint   `json:"id"`
	Name string `json:"name"`
}

type cardBody struct {
	ID       uint     `json:"id"`
	Question string   `json:"question"`
	Hint     string   `json:"hint"`
	Options  []string `json:"options"`
	Correct  []int    `json:"correct"`
}

func newCoreTestApp(t *testing.T) *coreTestApp {
	t.Helper()

	gin.SetMode(gin.TestMode)

	dbName := strings.NewReplacer("/", "_", " ", "_").Replace(t.Name())
	testDB, err := gorm.Open(sqlite.Open("file:"+dbName+"?mode=memory&cache=shared"), &gorm.Config{})
	if err != nil {
		t.Fatalf("open test db: %v", err)
	}

	sqlDB, err := testDB.DB()
	if err != nil {
		t.Fatalf("get sql db: %v", err)
	}
	sqlDB.SetMaxOpenConns(1)

	if err := testDB.Exec("PRAGMA foreign_keys = ON").Error; err != nil {
		t.Fatalf("enable foreign keys: %v", err)
	}

	createTestSchema(t, testDB)
	db.DB = testDB

	app := &coreTestApp{
		db:    testDB,
		t:     t,
		users: make(map[string]models.User),
		packs: make(map[string]models.Pack),
		cards: make(map[string]models.Card),
	}

	router := gin.New()
	router.GET("/core/share/:share_code", GetSharedPack)

	protected := router.Group("/core")
	protected.Use(func(c *gin.Context) {
		userID, _ := strconv.Atoi(c.GetHeader("X-Test-User-ID"))
		if userID == 0 {
			userID = int(app.users["owner"].ID)
		}
		c.Set("user_id", userID)
		c.Set("username", c.GetHeader("X-Test-Username"))
		c.Next()
	})

	protected.POST("/pack", CreatePack)
	protected.PATCH("/pack/:pack_id", UpdatePack)
	protected.DELETE("/pack/:pack_id", DeletePack)
	protected.GET("/packs", GetPacks)
	protected.POST("/pack/fork/:share_code", ForkPack)
	protected.POST("/pack/:share_code", AddSharedPack)
	protected.POST("/cards/:pack_id", cards.CreateCards)
	protected.PATCH("/cards", cards.UpdateCards)
	protected.DELETE("/cards", cards.DeleteCard)
	protected.GET("/cards/:pack_id", cards.GetCards)

	app.router = router
	app.users["owner"] = app.createUser(t, "owner")
	app.users["guest"] = app.createUser(t, "guest")

	return app
}

func createTestSchema(t *testing.T, testDB *gorm.DB) {
	t.Helper()

	statements := []string{
		`CREATE TABLE users (
			id integer PRIMARY KEY AUTOINCREMENT,
			name varchar(30) NOT NULL,
			pass_hash text NOT NULL
		)`,
		`CREATE TABLE packs (
			id integer PRIMARY KEY AUTOINCREMENT,
			name varchar(50) NOT NULL,
			description text,
			creation_date datetime NOT NULL,
			updating_date datetime,
			share_code varchar(64) NOT NULL UNIQUE,
			author_id integer NOT NULL,
			FOREIGN KEY (author_id) REFERENCES users(id)
		)`,
		`CREATE TABLE pack_permissions (
			user_id integer NOT NULL,
			pack_id integer NOT NULL,
			permission integer NOT NULL,
			PRIMARY KEY (user_id, pack_id),
			FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
			FOREIGN KEY (pack_id) REFERENCES packs(id) ON DELETE CASCADE
		)`,
		`CREATE TABLE cards (
			id integer PRIMARY KEY AUTOINCREMENT,
			question text NOT NULL,
			pack_id integer NOT NULL,
			hint text NOT NULL,
			FOREIGN KEY (pack_id) REFERENCES packs(id) ON DELETE CASCADE
		)`,
		`CREATE TABLE card_options (
			id integer PRIMARY KEY AUTOINCREMENT,
			content text NOT NULL,
			is_right boolean NOT NULL,
			card_id integer NOT NULL,
			FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
		)`,
		`CREATE TABLE forks (
			fork_id integer NOT NULL,
			original_id integer NOT NULL,
			PRIMARY KEY (fork_id, original_id)
		)`,
		`CREATE TABLE comments (
			id integer PRIMARY KEY AUTOINCREMENT,
			user_id integer NOT NULL,
			creation_date datetime,
			updating_date datetime,
			pack_id integer NOT NULL,
			content text NOT NULL
		)`,
		`CREATE TABLE replies (
			initial_id integer NOT NULL,
			reply_id integer NOT NULL,
			PRIMARY KEY (initial_id, reply_id)
		)`,
		`CREATE TABLE published_pack (
			id integer PRIMARY KEY,
			user_id integer NOT NULL,
			rating real,
			subject varchar(30),
			university varchar(30),
			professor varchar(60),
			course_book varchar(80)
		)`,
	}

	for _, statement := range statements {
		if err := testDB.Exec(statement).Error; err != nil {
			t.Fatalf("create raw table: %v", err)
		}
	}
}

func Test_CoreHandlersIntegration(t *testing.T) {
	testData := map[string][]string{
		"create pack grants owner permission": {"owner", "Biology"},
		"create pack with cards":              {"owner", "Physics", "Mechanics basics"},
		"create and get cards":                {"owner", "Biology"},
		"update card":                         {"owner", "Biology", "Updated question"},
		"get packs by permission":             {"owner", "guest", "Shared"},
		"share adds pack permission":          {"owner", "guest", "Shared"},
		"fork creates deep copy":              {"owner", "guest", "Original"},
		"delete card removes options":         {"owner", "Biology"},
		"delete pack removes deep data":       {"owner", "Biology"},
		"reject bad card request":             {"owner", "Biology"},
	}

	expectedData := map[string][]string{
		"create pack grants owner permission": {"1"},
		"create pack with cards":              {"1", "2"},
		"create and get cards":                {"1"},
		"update card":                         {"Updated question"},
		"get packs by permission":             {"1"},
		"share adds pack permission":          {"2"},
		"fork creates deep copy":              {"1"},
		"delete card removes options":         {"0"},
		"delete pack removes deep data":       {"0"},
		"reject bad card request":             {"400"},
	}

	tests := []Test{
		{
			name: "create pack grants owner permission",
			verifyResult: func(t *testing.T, p *coreTestApp, testName string) {
				resp, body := p.postJSON(t, "/core/pack", map[string]string{"name": testData[testName][1]}, "owner")
				assertStatus(t, resp, http.StatusCreated)

				if body.ShareCode == "" {
					t.Fatal("expected share code")
				}

				var permission models.PackPermission
				if err := p.db.Where("user_id = ? AND pack_id = ?", p.users["owner"].ID, body.ID).First(&permission).Error; err != nil {
					t.Fatalf("find permission: %v", err)
				}
				if permission.Permission != 1 {
					t.Fatalf("expected permission 1, got %d", permission.Permission)
				}
			},
		},
		{
			name: "create pack with cards",
			verifyResult: func(t *testing.T, p *coreTestApp, testName string) {
				payload := cardsPayload()
				payload["name"] = testData[testName][1]
				payload["description"] = testData[testName][2]

				resp, body := p.postJSON(t, "/core/pack", payload, "owner")
				assertStatus(t, resp, http.StatusCreated)

				if body.Description == nil || *body.Description != testData[testName][2] {
					t.Fatalf("expected description %q, got %#v", testData[testName][2], body.Description)
				}
				if len(body.Cards) != mustAtoi(t, expectedData[testName][0]) {
					t.Fatalf("expected one created card, got %#v", body.Cards)
				}
				if body.Cards[0].ID == 0 {
					t.Fatal("expected created card id")
				}
				if len(body.Cards[0].Options) != mustAtoi(t, expectedData[testName][1]) {
					t.Fatalf("expected two options, got %#v", body.Cards[0].Options)
				}
				if len(body.Cards[0].Correct) != 1 || body.Cards[0].Correct[0] != 1 {
					t.Fatalf("unexpected correct indexes: %#v", body.Cards[0].Correct)
				}

				var cardsCount int64
				p.db.Model(&models.Card{}).Where("pack_id = ?", body.ID).Count(&cardsCount)
				if cardsCount != int64(mustAtoi(t, expectedData[testName][0])) {
					t.Fatalf("expected one persisted card, got %d", cardsCount)
				}

				var optionsCount int64
				p.db.Model(&models.CardOption{}).Where("card_id = ?", body.Cards[0].ID).Count(&optionsCount)
				if optionsCount != int64(mustAtoi(t, expectedData[testName][1])) {
					t.Fatalf("expected two persisted options, got %d", optionsCount)
				}
			},
		},
		{
			name: "create and get cards",
			performAction: func(p *coreTestApp, testName string) {
				p.packs["main"] = p.createPack(p.t, testData[testName][1], "owner")
			},
			verifyResult: func(t *testing.T, p *coreTestApp, testName string) {
				createResp, createBody := p.postJSON(t, "/core/cards/"+itoa(p.packs["main"].ID), cardsPayload(), "owner")
				assertStatus(t, createResp, http.StatusOK)
				if len(createBody.Cards) != 1 || len(createBody.Cards[0].Options) != 2 {
					t.Fatalf("unexpected created cards response: %#v", createBody.Cards)
				}

				getResp, getBody := p.get(t, "/core/cards/"+itoa(p.packs["main"].ID), "owner")
				assertStatus(t, getResp, http.StatusOK)
				if len(getBody.Cards) != mustAtoi(t, expectedData[testName][0]) {
					t.Fatalf("expected one card, got %#v", getBody.Cards)
				}
				if len(getBody.Cards[0].Correct) != 1 || getBody.Cards[0].Correct[0] != 1 {
					t.Fatalf("unexpected correct indexes: %#v", getBody.Cards[0].Correct)
				}
			},
		},
		{
			name: "update card",
			performAction: func(p *coreTestApp, testName string) {
				p.packs["main"] = p.createPack(p.t, testData[testName][1], "owner")
				p.cards["main"] = p.createCard(p.t, p.packs["main"].ID)
			},
			verifyResult: func(t *testing.T, p *coreTestApp, testName string) {
				resp, body := p.patchJSON(t, "/core/cards", map[string]any{
					"cards": []map[string]any{
						{"id": p.cards["main"].ID, "question": testData[testName][2], "correct": []int{0}},
					},
				}, "owner")
				assertStatus(t, resp, http.StatusOK)

				if body.Cards[0].Question != expectedData[testName][0] {
					t.Fatalf("expected updated question, got %q", body.Cards[0].Question)
				}
				if body.Cards[0].Correct[0] != 0 {
					t.Fatalf("expected correct index 0, got %#v", body.Cards[0].Correct)
				}
			},
		},
		{
			name: "get packs by permission",
			performAction: func(p *coreTestApp, testName string) {
				p.packs["shared"] = p.createPack(p.t, testData[testName][2], "owner")
				p.addPermission(p.t, "guest", p.packs["shared"].ID, 2)
			},
			verifyResult: func(t *testing.T, p *coreTestApp, testName string) {
				resp, body := p.get(t, "/core/packs", "guest")
				assertStatus(t, resp, http.StatusOK)

				if len(body.Packs) != mustAtoi(t, expectedData[testName][0]) {
					t.Fatalf("expected shared pack in list, got %#v", body.Packs)
				}
				if body.Packs[0].Author.Name != "owner" {
					t.Fatalf("expected real author owner, got %q", body.Packs[0].Author.Name)
				}
			},
		},
		{
			name: "share adds pack permission",
			performAction: func(p *coreTestApp, testName string) {
				p.packs["shared"] = p.createPack(p.t, testData[testName][2], "owner")
			},
			verifyResult: func(t *testing.T, p *coreTestApp, testName string) {
				resp, body := p.postJSON(t, "/core/pack/"+p.packs["shared"].ShareCode, nil, "guest")
				assertStatus(t, resp, http.StatusOK)
				if body.ID != p.packs["shared"].ID {
					t.Fatalf("expected shared pack id %d, got %d", p.packs["shared"].ID, body.ID)
				}

				var permission models.PackPermission
				if err := p.db.Where("user_id = ? AND pack_id = ?", p.users["guest"].ID, p.packs["shared"].ID).First(&permission).Error; err != nil {
					t.Fatalf("find shared permission: %v", err)
				}
				if permission.Permission != mustAtoi(t, expectedData[testName][0]) {
					t.Fatalf("expected permission 2, got %d", permission.Permission)
				}
			},
		},
		{
			name: "fork creates deep copy",
			performAction: func(p *coreTestApp, testName string) {
				p.packs["original"] = p.createPack(p.t, testData[testName][2], "owner")
				p.createCard(p.t, p.packs["original"].ID)
			},
			verifyResult: func(t *testing.T, p *coreTestApp, testName string) {
				resp, body := p.postJSON(t, "/core/pack/fork/"+p.packs["original"].ShareCode, nil, "guest")
				assertStatus(t, resp, http.StatusCreated)

				if body.ID == p.packs["original"].ID {
					t.Fatal("expected fork to have new id")
				}
				if len(body.Cards) != mustAtoi(t, expectedData[testName][0]) {
					t.Fatalf("expected copied cards, got %#v", body.Cards)
				}

				var fork models.Fork
				if err := p.db.Where("fork_id = ? AND original_id = ?", body.ID, p.packs["original"].ID).First(&fork).Error; err != nil {
					t.Fatalf("find fork row: %v", err)
				}
			},
		},
		{
			name: "delete card removes options",
			performAction: func(p *coreTestApp, testName string) {
				p.packs["main"] = p.createPack(p.t, testData[testName][1], "owner")
				p.cards["main"] = p.createCard(p.t, p.packs["main"].ID)
			},
			verifyResult: func(t *testing.T, p *coreTestApp, testName string) {
				resp, _ := p.deleteJSON(t, "/core/cards", map[string]any{"cards": []uint{p.cards["main"].ID}}, "owner")
				assertStatus(t, resp, http.StatusOK)

				var count int64
				p.db.Model(&models.CardOption{}).Where("card_id = ?", p.cards["main"].ID).Count(&count)
				if count != int64(mustAtoi(t, expectedData[testName][0])) {
					t.Fatalf("expected no options, got %d", count)
				}
			},
		},
		{
			name: "delete pack removes deep data",
			performAction: func(p *coreTestApp, testName string) {
				p.packs["main"] = p.createPack(p.t, testData[testName][1], "owner")
				p.createCard(p.t, p.packs["main"].ID)
			},
			verifyResult: func(t *testing.T, p *coreTestApp, testName string) {
				resp, _ := p.delete(t, "/core/pack/"+itoa(p.packs["main"].ID), "owner")
				assertStatus(t, resp, http.StatusOK)

				var count int64
				p.db.Model(&models.Card{}).Where("pack_id = ?", p.packs["main"].ID).Count(&count)
				if count != int64(mustAtoi(t, expectedData[testName][0])) {
					t.Fatalf("expected no cards, got %d", count)
				}
			},
		},
		{
			name: "reject bad card request",
			performAction: func(p *coreTestApp, testName string) {
				p.packs["main"] = p.createPack(p.t, testData[testName][1], "owner")
			},
			verifyResult: func(t *testing.T, p *coreTestApp, testName string) {
				resp, _ := p.postJSON(t, "/core/cards/"+itoa(p.packs["main"].ID), map[string]any{
					"cards": []map[string]any{
						{"question": "", "hint": "hint", "options": []string{"a"}, "correct": []int{0}},
					},
				}, "owner")
				assertStatus(t, resp, mustAtoi(t, expectedData[testName][0]))
			},
		},
	}

	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			p := newCoreTestApp(t)
			if test.performAction != nil {
				test.performAction(p, test.name)
			}
			test.verifyResult(t, p, test.name)
		})
	}
}

func (p *coreTestApp) createUser(t *testing.T, name string) models.User {
	t.Helper()

	user := models.User{Name: name, PassHash: "hash"}
	if err := p.db.Create(&user).Error; err != nil {
		t.Fatalf("create user: %v", err)
	}
	return user
}

func (p *coreTestApp) createPack(t *testing.T, name string, username string) models.Pack {
	t.Helper()

	var pack models.Pack
	if err := p.db.Transaction(func(tx *gorm.DB) error {
		var err error
		pack, err = sc.CreatePackWithOwnerPermission(tx, name, nil, p.users[username].ID)
		return err
	}); err != nil {
		t.Fatalf("create pack: %v", err)
	}

	if err := p.db.Preload("Author").First(&pack, pack.ID).Error; err != nil {
		t.Fatalf("reload pack: %v", err)
	}
	return pack
}

func (p *coreTestApp) createCard(t *testing.T, packID uint) models.Card {
	t.Helper()

	card := models.Card{Question: "Question", Hint: "Hint", PackID: packID}
	if err := p.db.Create(&card).Error; err != nil {
		t.Fatalf("create card: %v", err)
	}
	options := []models.CardOption{
		{Content: "a", IsRight: false, CardID: card.ID},
		{Content: "b", IsRight: true, CardID: card.ID},
	}
	if err := p.db.Create(&options).Error; err != nil {
		t.Fatalf("create options: %v", err)
	}
	card.Options = options
	return card
}

func (p *coreTestApp) addPermission(t *testing.T, username string, packID uint, permission int) {
	t.Helper()

	if err := p.db.Create(&models.PackPermission{
		UserID:     p.users[username].ID,
		PackID:     packID,
		Permission: permission,
	}).Error; err != nil {
		t.Fatalf("add permission: %v", err)
	}
}

func cardsPayload() map[string]any {
	return map[string]any{
		"cards": []map[string]any{
			{
				"question": "Question",
				"hint":     "Hint",
				"options":  []string{"a", "b"},
				"correct":  []int{1},
			},
		},
	}
}

func (p *coreTestApp) postJSON(t *testing.T, path string, body any, username string) (*httptest.ResponseRecorder, coreResponse) {
	t.Helper()
	return p.requestJSON(t, http.MethodPost, path, body, username)
}

func (p *coreTestApp) patchJSON(t *testing.T, path string, body any, username string) (*httptest.ResponseRecorder, coreResponse) {
	t.Helper()
	return p.requestJSON(t, http.MethodPatch, path, body, username)
}

func (p *coreTestApp) get(t *testing.T, path string, username string) (*httptest.ResponseRecorder, coreResponse) {
	t.Helper()
	return p.requestJSON(t, http.MethodGet, path, nil, username)
}

func (p *coreTestApp) delete(t *testing.T, path string, username string) (*httptest.ResponseRecorder, coreResponse) {
	t.Helper()
	return p.requestJSON(t, http.MethodDelete, path, nil, username)
}

func (p *coreTestApp) deleteJSON(t *testing.T, path string, body any, username string) (*httptest.ResponseRecorder, coreResponse) {
	t.Helper()
	return p.requestJSON(t, http.MethodDelete, path, body, username)
}

func (p *coreTestApp) requestJSON(t *testing.T, method string, path string, body any, username string) (*httptest.ResponseRecorder, coreResponse) {
	t.Helper()

	var payload []byte
	if body != nil {
		var err error
		payload, err = json.Marshal(body)
		if err != nil {
			t.Fatalf("marshal body: %v", err)
		}
	}

	req := httptest.NewRequest(method, path, bytes.NewReader(payload))
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("X-Test-User-ID", itoa(p.users[username].ID))
	req.Header.Set("X-Test-Username", username)

	resp := httptest.NewRecorder()
	p.router.ServeHTTP(resp, req)

	var parsed coreResponse
	if strings.TrimSpace(resp.Body.String()) != "" {
		if err := json.Unmarshal(resp.Body.Bytes(), &parsed); err != nil {
			t.Fatalf("unmarshal response %q: %v", resp.Body.String(), err)
		}
	}

	return resp, parsed
}

func assertStatus(t *testing.T, resp *httptest.ResponseRecorder, expected int) {
	t.Helper()

	if resp.Code != expected {
		t.Fatalf("expected status %d, got %d body=%s", expected, resp.Code, resp.Body.String())
	}
}

func itoa(value uint) string {
	return strconv.FormatUint(uint64(value), 10)
}

func mustAtoi(t *testing.T, value string) int {
	t.Helper()

	result, err := strconv.Atoi(value)
	if err != nil {
		t.Fatalf("parse int %q: %v", value, err)
	}
	return result
}
