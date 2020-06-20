package leave

import (
	"github.com/Dyescape/DyescapeBot/pkg/model"
	"github.com/Dyescape/DyescapeBot/pkg/server/discord"
	"github.com/bwmarrin/discordgo"
)

// DiscordLeave struct represents the leave service implementation for Discord.
type DiscordLeave struct {
	*discord.Discord
	Handle Handle
}

// NewDiscordLeave accepts a Discord pointer, and a handling function for when a member leaves.
func NewDiscordLeave(server *discord.Discord, handle Handle) *DiscordLeave {
	return &DiscordLeave{
		Discord: server,
		Handle:  handle,
	}
}

// Initialise adds the member leave handler to discordgo.
func (dl *DiscordLeave) Initialise() error {
	dl.Session.AddHandler(dl.addMember)
	return nil
}

// addMember is the abstract handler which takes the user ID and the server ID and invokes the handler.
func (dl *DiscordLeave) addMember(s *discordgo.Session, e *discordgo.GuildMemberRemove) {
	user := &model.User{ID: e.User.ID}
	dl.Handle(user, e.GuildID)
}
