package discord

import (
	"fmt"

	"github.com/bwmarrin/discordgo"
)

func (s *Service) SendEmbed(channel, title, message, footer string) {
	_, err := s.Session.ChannelMessageSendEmbed(channel, &discordgo.MessageEmbed{
		Title:       title,
		Description: message,
		Footer: &discordgo.MessageEmbedFooter{
			Text: footer,
		},
	})
	// TODO: Logger
	if err != nil {
		fmt.Println(err.Error())
	}
}
