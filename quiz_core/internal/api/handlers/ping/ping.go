package ping_handlers

import "github.com/gin-gonic/gin"


func Pong(ctx *gin.Context) {
	ctx.String(200, "Pong")
}
