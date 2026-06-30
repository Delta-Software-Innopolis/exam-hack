package utils

import (
	"auth/internal/config"
	"crypto/rand"
	"crypto/rsa"
	"testing"
)

type Test struct {
	name          string
	performAction func(*tokenTestState, string)
	verifyResult  func(*testing.T, *tokenTestState, string)
}

type tokenTestState struct {
	accessToken  string
	refreshToken string
	hash         string
	err          error
	userID       int
}

func newTokenTestState(t *testing.T) *tokenTestState {
	t.Helper()

	secret, err := rsa.GenerateKey(rand.Reader, 2048)
	if err != nil {
		t.Fatalf("generate rsa key: %v", err)
	}

	config.AppConfig.Secret = secret

	return &tokenTestState{}
}

func Test_TokenUtilities(t *testing.T) {
	testData := map[string][]string{
		"hash token":         {"refresh-token"},
		"generate tokens":    {"42", "alice"},
		"reject wrong token": {"not-a-jwt"},
	}

	expectedData := map[string][]string{
		"hash token":         {"0eb17643d4e9261163783a420859c92c7d212fa9624106a12b510afbec266120"},
		"generate tokens":    {"42"},
		"reject wrong token": {"error"},
	}

	tests := []Test{
		{
			name: "hash token",
			performAction: func(p *tokenTestState, testName string) {
				p.hash = HashToken(testData[testName][0])
			},
			verifyResult: func(t *testing.T, p *tokenTestState, testName string) {
				if p.hash != expectedData[testName][0] {
					t.Fatalf("expected hash %q, got %q", expectedData[testName][0], p.hash)
				}
			},
		},
		{
			name: "generate tokens",
			performAction: func(p *tokenTestState, testName string) {
				p.accessToken, p.refreshToken, p.err = GenerateTokens(42, testData[testName][1])
			},
			verifyResult: func(t *testing.T, p *tokenTestState, testName string) {
				if p.err != nil {
					t.Fatalf("generate tokens: %v", p.err)
				}

				if err := ValidateAccessToken(p.accessToken); err != nil {
					t.Fatalf("validate access token: %v", err)
				}

				userID, err := ValidateRefreshToken(p.refreshToken)
				if err != nil {
					t.Fatalf("validate refresh token: %v", err)
				}

				if got, want := userID, 42; got != want {
					t.Fatalf("expected user id %d, got %d", want, got)
				}
			},
		},
		{
			name: "reject wrong token",
			performAction: func(p *tokenTestState, testName string) {
				p.userID, p.err = ValidateRefreshToken(testData[testName][0])
			},
			verifyResult: func(t *testing.T, p *tokenTestState, testName string) {
				if p.err == nil {
					t.Fatal("expected invalid token error")
				}

				if p.userID != 0 {
					t.Fatalf("expected empty user id, got %d", p.userID)
				}
			},
		},
	}

	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			p := newTokenTestState(t)
			if test.performAction != nil {
				test.performAction(p, test.name)
			}
			test.verifyResult(t, p, test.name)
		})
	}
}
