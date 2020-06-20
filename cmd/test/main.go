package main

import (
	"fmt"
	"strings"

	"github.com/Dyescape/DyescapeBot/pkg/model"
	"github.com/Dyescape/DyescapeBot/pkg/server"
	"github.com/Dyescape/DyescapeBot/pkg/server/discord"
	"github.com/Dyescape/DyescapeBot/pkg/server/service/message"
)

func main() {
	srv := discord.NewDiscord("Bot ")
	srv.RegisterService(message.NewDiscordMessage(srv, hello))
	if err := srv.Start(); err != nil {
		panic(err)
	}

	fmt.Println("Bot is now running. Press CTRL-C to exit.")
	server.AwaitShutdown()
}

func hello(user *model.User, content string) *message.Response {
	if !strings.Contains(content, "<@!418534594468380672>") {
		return nil
	}
	return &message.Response{
		Message: fmt.Sprintf("Hello <@%v>!", user.ID),
		Type:    message.Raw,
	}
}
