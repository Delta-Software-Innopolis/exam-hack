package config

import (
	"crypto/rsa"
	"os"
	"strings"

	"github.com/golang-jwt/jwt/v5"
)

type Config struct {
	Routes struct {
		CORSAddresses []string
	}

	Database struct {
		Host      string
		User     string
		Password string
		Name     string
	}

	Secret *rsa.PrivateKey
	IsHttps bool
}

var AppConfig Config

func Load() {
	AppConfig.Routes.CORSAddresses = parseCommaSeparatedEnv("CORS_ADDRS")

	AppConfig.Database.Host = os.Getenv("POSTGRES_HOST")
	AppConfig.Database.User = os.Getenv("POSTGRES_USER")
	AppConfig.Database.Password = os.Getenv("POSTGRES_PASSWORD")
	AppConfig.Database.Name = os.Getenv("POSTGRES_DB")

	AppConfig.IsHttps = os.Getenv("IS_HTTPS") != "0"

	privateKeyBytes, err := os.ReadFile(os.Getenv("SECRET_FILE"))
	if err != nil {
		panic(err)
	}

	private, err := jwt.ParseRSAPrivateKeyFromPEM(privateKeyBytes)

	if err != nil {
		panic(err)
	}

	AppConfig.Secret = private

}

func parseCommaSeparatedEnv(name string) []string {
	values := strings.Split(os.Getenv(name), ",")
	result := make([]string, 0, len(values))

	for _, value := range values {
		value = strings.TrimSpace(value)
		if value != "" {
			result = append(result, value)
		}
	}

	return result
}
