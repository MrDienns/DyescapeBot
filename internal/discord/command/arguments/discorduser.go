package arguments

import (
	"strings"

	"github.com/bwmarrin/discordgo"
)

// IntegerResolver is a resolver that will try to transform the argument into an integer.
type DiscordUserResolver struct {
	session *discordgo.Session
}

func NewDiscordUserResolver(s *discordgo.Session) *DiscordUserResolver {
	return &DiscordUserResolver{session: s}
}

// Resolve will try to parse the string as integer. If it failed, an error is returned.
func (dur DiscordUserResolver) Resolve(arg string) (interface{}, error) {
	u, err := dur.session.User(parseUserString(arg))
	if err != nil {
		return nil, err
	}
	return u, nil
}

func parseUserString(arg string) string {
	arg = strings.TrimLeft(arg, "<@!")
	arg = strings.TrimRight(arg, ">")
	return arg
}
