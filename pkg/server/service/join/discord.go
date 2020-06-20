package join

import (
	"github.com/Dyescape/DyescapeBot/pkg/model"
	"github.com/Dyescape/DyescapeBot/pkg/server/discord"
	"github.com/bwmarrin/discordgo"
)

// DiscordJoin struct represents the join service implementation for Discord.
type DiscordJoin struct {
	*discord.Discord
	Handle Handle
}

// NewDiscordJoin accepts a Discord pointer, and a handling function for when a member joins.
func NewDiscordJoin(server *discord.Discord, handle Handle) *DiscordJoin {
	return &DiscordJoin{
		Discord: server,
		Handle:  handle,
	}
}

// Initialise adds the member join handler to discordgo.
func (dj *DiscordJoin) Initialise() error {
	dj.Session.AddHandler(dj.addMember)
	return nil
}

// addMember is the abstract handler which takes the user ID and the server ID and invokes the handler.
func (dj *DiscordJoin) addMember(s *discordgo.Session, e *discordgo.GuildMemberAdd) {
	user := &model.User{ID: e.User.ID}
	dj.Handle(user, e.GuildID)
}
