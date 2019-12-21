package discord

import (
	"fmt"

	"github.com/bwmarrin/discordgo"
)

func (s *Service) SendEmbed(channel, title, message, footer string) {
	_, err := s.Session.ChannelMessageSendEmbed(channel, embed(title, message, footer))
	if err != nil {
		s.Logger.Error(err.Error())
	}
}

func embed(title, message, footer string) *discordgo.MessageEmbed {
	return &discordgo.MessageEmbed{
		Title:       fmt.Sprintf("**%s**", title),
		Description: message,
		Footer: &discordgo.MessageEmbedFooter{
			Text: footer,
		},
	}
}
