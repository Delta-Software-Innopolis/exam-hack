package ping_handlers

import (
	"log"
	"github.com/gin-gonic/gin"
)


func Pong(ctx *gin.Context) {
	log.Println("hi from ping!")
	ctx.String(200, "Pong")
}
