package discord

import (
	"github.com/Dyescape/DyescapeBot/internal/app/log"
	"github.com/bwmarrin/discordgo"
)

// Service strict is used as a base struct for the Discord connection to the
// gateway. Contains a public *discordgo.Session field.
type Service struct {
	Session *discordgo.Session
	Logger  *log.Logger
	token   string
	Module  string
}

// NewService accepts a Discord API token and constructs a new Service struct.
func NewService(token, module string, logger *log.Logger) *Service {
	return &Service{
		Logger: logger,
		token:  token,
		Module: module,
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
	s.Logger.Info("Connecting to Discord gateway")
	return sess.Open()
}
