package discord

import (
	"fmt"

	"github.com/bwmarrin/discordgo"
)

func (s *Service) SendEmbed(channel, title, message, footer string) {
	_, err := s.Session.ChannelMessageSendEmbed(channel, &discordgo.MessageEmbed{
		Title:       fmt.Sprintf("**%s**", title),
		Description: message,
		Footer: &discordgo.MessageEmbedFooter{
			Text: footer,
		},
	})
	if err != nil {
		s.Logger.Error(err.Error())
	}
}
