package config

import (
	"os"
	"quiz_core/pkg/utils"
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

}

var AppConfig Config

func Load() {
	AppConfig.Routes.CORSAddresses = utils.ParseCommaSeparatedEnv("CORS_ADDRS")

	AppConfig.Host = ":8080"

	AppConfig.Database.Host = os.Getenv("POSTGRES_HOST")
	AppConfig.Database.User = os.Getenv("POSTGRES_USER")
	AppConfig.Database.Password = os.Getenv("POSTGRES_PASSWORD")
	AppConfig.Database.Name = os.Getenv("POSTGRES_DB")


}

