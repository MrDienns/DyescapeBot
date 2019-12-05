package discord

import (
	"github.com/bwmarrin/discordgo"
)

// Service strict is used as a base struct for the Discord connection to the
// gateway. Contains a public *discordgo.Session field.
type Service struct {
	token   string
	Session *discordgo.Session
}

// NewService accepts a Discord API token and constructs a new Service struct.
func NewService(token string) *Service {
	return &Service{
		token: token,
	}
}

// Connect opens a connection to the Discord gateway through discordgo. An
// error may be returned if anything went wrong.
func (s *Service) Connect() error {
	sess, err := discordgo.New(s.token)
	if err != nil {
		return err
	}

	s.Session = sess
	return sess.Open()
}
