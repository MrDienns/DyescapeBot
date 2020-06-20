package message

import (
	"github.com/Dyescape/DyescapeBot/pkg/model"
	"github.com/Dyescape/DyescapeBot/pkg/server/discord"
	"github.com/bwmarrin/discordgo"
)

// DiscordMessage struct represents an implementation of the abstract Message service.
type DiscordMessage struct {
	*discord.Discord
	Handle Handle
}

// NewDiscordMessage takes a pointer to a Discord server and a handle function.
func NewDiscordMessage(server *discord.Discord, handle Handle) *DiscordMessage {
	return &DiscordMessage{
		Discord: server,
		Handle:  handle,
	}
}

// Initialise will setup the Discord message handler
func (dm *DiscordMessage) Initialise() error {
	dm.Session.AddHandler(dm.readMessage)
	return nil
}

// readMessage is the function that gets invoked by discordgo when a message is sent from a user.
func (dm *DiscordMessage) readMessage(s *discordgo.Session, m *discordgo.MessageCreate) {
	user := &model.User{ID: m.Author.ID}
	resp := dm.Handle(user, m.Content)
	if resp != nil {
		if resp.Type == Raw {
			s.ChannelMessageSend(m.ChannelID, resp.Message)
		} else if resp.Type == Embed {
			s.ChannelMessageSendEmbed(m.ChannelID, &discordgo.MessageEmbed{
				Title:       resp.Title,
				Description: resp.Message,
			})
		}
	}
}
