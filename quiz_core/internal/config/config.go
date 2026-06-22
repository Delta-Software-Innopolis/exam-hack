package config

import (
	"crypto/rsa"
	"log"
	"os"
	"quiz_core/pkg/utils"

	"github.com/golang-jwt/jwt/v5"
)


type Config struct {
	Database struct {
		Host     string
		User     string
		Password string
		Name     string
	}

	Routes struct {
		CORSAddresses []string
	}

	Host string

	LLMService struct {
		URL string
	}

	AuthPublic *rsa.PublicKey
}

var AppConfig Config

func Load() {
	AppConfig.Routes.CORSAddresses = utils.ParseCommaSeparatedEnv("CORS_ADDRS")

	AppConfig.Host = ":8001"

	AppConfig.LLMService.URL = os.Getenv("LLM_SERVICE_URL")
	if AppConfig.LLMService.URL == "" {
		AppConfig.LLMService.URL = "http://localhost:7000"
	}

	AppConfig.Database.Host = os.Getenv("POSTGRES_HOST")
	AppConfig.Database.User = os.Getenv("POSTGRES_USER")
	AppConfig.Database.Password = os.Getenv("POSTGRES_PASSWORD")
	AppConfig.Database.Name = os.Getenv("POSTGRES_DB")

	keyPath := os.Getenv("JWT_PUBLIC_KEY_FILE")
	if keyPath == "" {
		log.Println("WARNING: JWT_PUBLIC_KEY_FILE not set, auth will reject all requests")
		AppConfig.AuthPublic = nil
	} else {
		publicKeyBytes, err := os.ReadFile(keyPath)
		if err != nil {
			log.Printf("WARNING: failed to read JWT public key: %v, auth will reject all requests", err)
			AppConfig.AuthPublic = nil
		} else {
			public, err := jwt.ParseRSAPublicKeyFromPEM(publicKeyBytes)
			if err != nil {
				log.Printf("WARNING: failed to parse JWT public key: %v, auth will reject all requests", err)
				AppConfig.AuthPublic = nil
			} else {
				AppConfig.AuthPublic = public
			}
		}
	}

}

