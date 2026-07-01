package api

import (
	"auth/internal/config"
	"auth/internal/database"
	"auth/internal/models"
	"auth/pkg/utils"
	"bytes"
	"crypto/rand"
	"crypto/rsa"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"strconv"
	"strings"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/glebarez/sqlite"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
)

type Test struct {
	name          string
	performAction func(*authTestApp, string)
	verifyResult  func(*testing.T, *authTestApp, string)
}

type authTestApp struct {
	router *gin.Engine
	db     *gorm.DB
}

type authResponse struct {
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
	Status       string `json:"status"`
	Error        string `json:"error"`
}

func newAuthTestApp(t *testing.T) *authTestApp {
	t.Helper()

	gin.SetMode(gin.TestMode)

	secret, err := rsa.GenerateKey(rand.Reader, 2048)
	if err != nil {
		t.Fatalf("generate rsa key: %v", err)
	}

	config.AppConfig.Secret = secret
	config.AppConfig.IsHttps = false

	dbName := strings.NewReplacer("/", "_", " ", "_").Replace(t.Name())
	db, err := gorm.Open(sqlite.Open("file:"+dbName+"?mode=memory&cache=shared"), &gorm.Config{})
	if err != nil {
		t.Fatalf("open test db: %v", err)
	}

	sqlDB, err := db.DB()
	if err != nil {
		t.Fatalf("get sql db: %v", err)
	}
	sqlDB.SetMaxOpenConns(1)

	if err := db.AutoMigrate(&models.User{}); err != nil {
		t.Fatalf("migrate test db: %v", err)
	}

	if err := db.Exec(`
		CREATE TABLE tokens (
			id integer PRIMARY KEY AUTOINCREMENT,
			user_id integer NOT NULL,
			refresh_token_hash text NOT NULL,
			is_revoked boolean NOT NULL,
			expires_at datetime NOT NULL,
			CONSTRAINT fk_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
		)
	`).Error; err != nil {
		t.Fatalf("create tokens table: %v", err)
	}

	database.DB = db

	router := gin.New()
	group := router.Group("/auth")
	group.POST("/reg", Register)
	group.POST("/login", Login)
	group.POST("/refresh", Refresh)
	group.POST("/validate", Validate)
	group.POST("/logout", Logout)

	return &authTestApp{
		router: router,
		db:     db,
	}
}

func Test_AuthHandlersIntegration(t *testing.T) {
	testData := map[string][]string{
		"register creates tokens and cookie": {"alice", "password"},
		"login creates tokens and cookie":    {"bob", "password"},
		"refresh reads cookie":               {"carol", "password"},
		"logout reads and clears cookie":     {"dave", "password"},
		"duplicate register is conflict":     {"erin", "password"},
		"validate access token":              {"frank", "password"},
	}

	expectedData := map[string][]string{
		"register creates tokens and cookie": {"200"},
		"login creates tokens and cookie":    {"200"},
		"refresh reads cookie":               {"200"},
		"logout reads and clears cookie":     {"200"},
		"duplicate register is conflict":     {"409"},
		"validate access token":              {"ok"},
	}

	tests := []Test{
		{
			name: "register creates tokens and cookie",
			verifyResult: func(t *testing.T, p *authTestApp, testName string) {
				resp, parsed := p.postJSON(t, "/auth/reg", map[string]string{
					"username": testData[testName][0],
					"password": testData[testName][1],
				}, nil)

				assertStatus(t, resp, http.StatusOK)
				assertTokens(t, parsed)
				assertRefreshCookie(t, resp, parsed.RefreshToken)

				var user models.User
				if err := p.db.Where("name = ?", testData[testName][0]).First(&user).Error; err != nil {
					t.Fatalf("find registered user: %v", err)
				}

				var storedToken models.Token
				if err := p.db.Where("user_id = ?", user.ID).First(&storedToken).Error; err != nil {
					t.Fatalf("find stored refresh token: %v", err)
				}

				if storedToken.RefreshTokenHash != utils.HashToken(parsed.RefreshToken) {
					t.Fatal("stored refresh token hash does not match response token")
				}
			},
		},
		{
			name: "login creates tokens and cookie",
			performAction: func(p *authTestApp, testName string) {
				p.createUser(t, testData[testName][0], testData[testName][1])
			},
			verifyResult: func(t *testing.T, p *authTestApp, testName string) {
				resp, parsed := p.postJSON(t, "/auth/login", map[string]string{
					"username": testData[testName][0],
					"password": testData[testName][1],
				}, nil)

				assertStatus(t, resp, http.StatusOK)
				assertTokens(t, parsed)
				assertRefreshCookie(t, resp, parsed.RefreshToken)
			},
		},
		{
			name: "refresh reads cookie",
			verifyResult: func(t *testing.T, p *authTestApp, testName string) {
				registerResp, registerBody := p.postJSON(t, "/auth/reg", map[string]string{
					"username": testData[testName][0],
					"password": testData[testName][1],
				}, nil)

				cookie := findCookie(t, registerResp, "refresh_token")
				resp, parsed := p.postJSON(t, "/auth/refresh", nil, []*http.Cookie{cookie})

				assertStatus(t, resp, http.StatusOK)
				assertTokens(t, parsed)
				assertRefreshCookie(t, resp, parsed.RefreshToken)

				var oldToken models.Token
				if err := p.db.Where("refresh_token_hash = ?", utils.HashToken(registerBody.RefreshToken)).First(&oldToken).Error; err != nil {
					t.Fatalf("find old token: %v", err)
				}

				if !oldToken.IsRevoked {
					t.Fatal("old refresh token was not revoked")
				}

			},
		},
		{
			name: "logout reads and clears cookie",
			verifyResult: func(t *testing.T, p *authTestApp, testName string) {
				registerResp, registerBody := p.postJSON(t, "/auth/reg", map[string]string{
					"username": testData[testName][0],
					"password": testData[testName][1],
				}, nil)

				cookie := findCookie(t, registerResp, "refresh_token")
				resp, parsed := p.postJSON(t, "/auth/logout", nil, []*http.Cookie{cookie})

				assertStatus(t, resp, http.StatusOK)
				if parsed.Status != "ok" {
					t.Fatalf("expected status ok, got %q", parsed.Status)
				}

				cleared := findCookie(t, resp, "refresh_token")
				if cleared.Value != "" || cleared.MaxAge >= 0 {
					t.Fatalf("expected cleared refresh cookie, got value=%q max_age=%d", cleared.Value, cleared.MaxAge)
				}

				var storedToken models.Token
				if err := p.db.Where("refresh_token_hash = ?", utils.HashToken(registerBody.RefreshToken)).First(&storedToken).Error; err != nil {
					t.Fatalf("find stored token: %v", err)
				}

				if !storedToken.IsRevoked {
					t.Fatal("refresh token was not revoked")
				}
			},
		},
		{
			name: "duplicate register is conflict",
			verifyResult: func(t *testing.T, p *authTestApp, testName string) {
				p.postJSON(t, "/auth/reg", map[string]string{
					"username": testData[testName][0],
					"password": testData[testName][1],
				}, nil)

				resp, parsed := p.postJSON(t, "/auth/reg", map[string]string{
					"username": testData[testName][0],
					"password": testData[testName][1],
				}, nil)

				assertStatus(t, resp, http.StatusConflict)
				if parsed.Error == "" {
					t.Fatal("expected duplicate register error")
				}
			},
		},
		{
			name: "validate access token",
			verifyResult: func(t *testing.T, p *authTestApp, testName string) {
				_, registerBody := p.postJSON(t, "/auth/reg", map[string]string{
					"username": testData[testName][0],
					"password": testData[testName][1],
				}, nil)

				resp, parsed := p.postJSON(t, "/auth/validate", map[string]string{
					"access_token": registerBody.AccessToken,
				}, nil)

				assertStatus(t, resp, http.StatusOK)
				if parsed.Status != expectedData[testName][0] {
					t.Fatalf("expected validate status %q, got %q", expectedData[testName][0], parsed.Status)
				}
			},
		},
	}

	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			p := newAuthTestApp(t)
			if test.performAction != nil {
				test.performAction(p, test.name)
			}
			test.verifyResult(t, p, test.name)
		})
	}
}

func (p *authTestApp) postJSON(t *testing.T, path string, body any, cookies []*http.Cookie) (*httptest.ResponseRecorder, authResponse) {
	t.Helper()

	var payload []byte
	if body != nil {
		var err error
		payload, err = json.Marshal(body)
		if err != nil {
			t.Fatalf("marshal request body: %v", err)
		}
	}

	req := httptest.NewRequest(http.MethodPost, path, bytes.NewReader(payload))
	req.Header.Set("Content-Type", "application/json")
	for _, cookie := range cookies {
		req.AddCookie(cookie)
	}

	resp := httptest.NewRecorder()
	p.router.ServeHTTP(resp, req)

	var parsed authResponse
	if strings.TrimSpace(resp.Body.String()) != "" {
		if err := json.Unmarshal(resp.Body.Bytes(), &parsed); err != nil {
			t.Fatalf("unmarshal response %q: %v", resp.Body.String(), err)
		}
	}

	return resp, parsed
}

func (p *authTestApp) createUser(t *testing.T, username string, password string) models.User {
	t.Helper()

	passHash, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		t.Fatalf("hash password: %v", err)
	}

	user := models.User{
		Name:     username,
		PassHash: string(passHash),
	}

	if err := p.db.Create(&user).Error; err != nil {
		t.Fatalf("create user: %v", err)
	}

	return user
}

func assertStatus(t *testing.T, resp *httptest.ResponseRecorder, expected int) {
	t.Helper()

	if resp.Code != expected {
		t.Fatalf("expected status %d, got %d body=%s", expected, resp.Code, resp.Body.String())
	}
}

func assertTokens(t *testing.T, parsed authResponse) {
	t.Helper()

	if parsed.AccessToken == "" {
		t.Fatal("expected access token")
	}

	if parsed.RefreshToken == "" {
		t.Fatal("expected refresh token")
	}
}

func assertRefreshCookie(t *testing.T, resp *httptest.ResponseRecorder, expectedValue string) {
	t.Helper()

	cookie := findCookie(t, resp, "refresh_token")
	if cookie.Value != expectedValue {
		t.Fatalf("expected refresh cookie %q, got %q", expectedValue, cookie.Value)
	}

	if !cookie.HttpOnly {
		t.Fatal("expected refresh cookie to be http only")
	}

	if cookie.MaxAge <= 0 {
		t.Fatalf("expected positive refresh cookie max age, got %d", cookie.MaxAge)
	}
}

func findCookie(t *testing.T, resp *httptest.ResponseRecorder, name string) *http.Cookie {
	t.Helper()

	for _, cookie := range resp.Result().Cookies() {
		if cookie.Name == name {
			return cookie
		}
	}

	t.Fatalf("cookie %q not found", name)
	return nil
}

func Test_AuthHandlersRejectBadInput(t *testing.T) {
	testData := map[string][]string{
		"register missing password": {"POST", "/auth/reg", `{"username":"alice"}`},
		"login wrong password":      {"POST", "/auth/login", `{"username":"bob","password":"wrong"}`},
		"refresh missing token":     {"POST", "/auth/refresh", `{}`},
		"logout missing token":      {"POST", "/auth/logout", `{}`},
		"validate missing token":    {"POST", "/auth/validate", `{}`},
	}

	expectedData := map[string][]string{
		"register missing password": {"400"},
		"login wrong password":      {"401"},
		"refresh missing token":     {"400"},
		"logout missing token":      {"400"},
		"validate missing token":    {"400"},
	}

	tests := []Test{
		{
			name: "register missing password",
			verifyResult: func(t *testing.T, p *authTestApp, testName string) {
				resp := p.rawPost(t, testData[testName][1], testData[testName][2])
				assertStatus(t, resp, expectedStatus(t, expectedData, testName))
			},
		},
		{
			name: "login wrong password",
			performAction: func(p *authTestApp, testName string) {
				p.createUser(t, "bob", "password")
			},
			verifyResult: func(t *testing.T, p *authTestApp, testName string) {
				resp := p.rawPost(t, testData[testName][1], testData[testName][2])
				assertStatus(t, resp, expectedStatus(t, expectedData, testName))
			},
		},
		{
			name: "refresh missing token",
			verifyResult: func(t *testing.T, p *authTestApp, testName string) {
				resp := p.rawPost(t, testData[testName][1], testData[testName][2])
				assertStatus(t, resp, expectedStatus(t, expectedData, testName))
			},
		},
		{
			name: "logout missing token",
			verifyResult: func(t *testing.T, p *authTestApp, testName string) {
				resp := p.rawPost(t, testData[testName][1], testData[testName][2])
				assertStatus(t, resp, expectedStatus(t, expectedData, testName))
			},
		},
		{
			name: "validate missing token",
			verifyResult: func(t *testing.T, p *authTestApp, testName string) {
				resp := p.rawPost(t, testData[testName][1], testData[testName][2])
				assertStatus(t, resp, expectedStatus(t, expectedData, testName))
			},
		},
	}

	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			p := newAuthTestApp(t)
			if test.performAction != nil {
				test.performAction(p, test.name)
			}

			test.verifyResult(t, p, test.name)
		})
	}
}

func (p *authTestApp) rawPost(t *testing.T, path string, body string) *httptest.ResponseRecorder {
	t.Helper()

	req := httptest.NewRequest(http.MethodPost, path, strings.NewReader(body))
	req.Header.Set("Content-Type", "application/json")

	resp := httptest.NewRecorder()
	p.router.ServeHTTP(resp, req)
	return resp
}

func expectedStatus(t *testing.T, expectedData map[string][]string, testName string) int {
	t.Helper()

	values := expectedData[testName]
	if len(values) == 0 {
		t.Fatalf("missing expected status for %q", testName)
	}

	status, err := strconv.Atoi(values[0])
	if err != nil {
		t.Fatalf("parse expected status for %q: %v", testName, err)
	}

	return status
}
