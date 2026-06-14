package utils

import (
	"os"
	"strings"
)


func ParseCommaSeparatedEnv(name string) []string {
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