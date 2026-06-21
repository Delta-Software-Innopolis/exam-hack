package config

import (
	"crypto/rsa"
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

	AuthPublic *rsa.PublicKey

}

var AppConfig Config

func Load() {
	AppConfig.Routes.CORSAddresses = utils.ParseCommaSeparatedEnv("CORS_ADDRS")

	AppConfig.Host = ":8001"

	AppConfig.Database.Host = os.Getenv("POSTGRES_HOST")
	AppConfig.Database.User = os.Getenv("POSTGRES_USER")
	AppConfig.Database.Password = os.Getenv("POSTGRES_PASSWORD")
	AppConfig.Database.Name = os.Getenv("POSTGRES_DB")

	publicKeyBytes, err := os.ReadFile(os.Getenv("JWT_PUBLIC_KEY_FILE"))
	if err != nil {
		panic(err)
	}

	public, err := jwt.ParseRSAPublicKeyFromPEM(publicKeyBytes)

	if err != nil {
		panic(err)
	}

	AppConfig.AuthPublic = public

}

