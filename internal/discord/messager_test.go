package discord

import (
	"testing"

	"github.com/stretchr/testify/assert"

	"github.com/bwmarrin/discordgo"
)

func TestService_embed(t *testing.T) {
	exp := &discordgo.MessageEmbed{
		Title:       "**title**",
		Description: "description",
		Footer: &discordgo.MessageEmbedFooter{
			Text: "footer",
		},
	}
	act := embed("title", "description", "footer")
	assert.Equal(t, exp, act)
}
