package service

import (
	"net/url"
	"reflect"

	"github.com/Dyescape/DyescapeBot/internal/app/command"

	"github.com/bwmarrin/discordgo"

	lib "github.com/Dyescape/DyescapeBot/internal/app/configuration"
)

// Service strict is used as a base struct for the Discord connection to the
// gateway. Contains a public discordgo.Session field.
type Service struct {
	token        string
	configReader lib.ConfigReader
	Session      *discordgo.Session
}

// NewService accepts a Discord API token and constructs a new Service struct.
func NewService(token string, configReader lib.ConfigReader) *Service {
	return &Service{
		token:        token,
		configReader: configReader,
	}
}

// Connect opens a connection to the Discord gateway through discordgo. An
// error may be returned if anything went wrong.
func (s *Service) Connect() error {
	sess, err := discordgo.New(s.token)
	if err != nil {
		return err
	}

	// Make and register our command
	suggestCmd := &command.Command{
		Name: "suggest",
		Args: map[string]reflect.Type{
			"url": reflect.TypeOf(url.URL{}),
		},
		Run: s.suggest}
	registry := command.NewRegistry()
	registry.Commands[suggestCmd.Name] = suggestCmd

	// Prepare our command handler
	configReader := lib.NewFlatFileConfigReader("command/suggestion")
	cmdHandler := command.NewHandler(configReader, command.NewConfiguration(configReader), registry,
		command.NewParser(registry))
	sess.AddHandler(cmdHandler.Handle)

	s.Session = sess
	return s.Session.Open()
}

// suggest is the command handling function for the suggestion command.
func (s *Service) suggest(sess *discordgo.Session, m *discordgo.MessageCreate, args map[string]interface{}) error {
	conf, err := Configuration(s.configReader, m.GuildID)
	if err != nil {
		return err
	}
	urlval := args["url"]
	if u, ok := urlval.(*url.URL); ok {
		suggestion := &Suggestion{
			Conf: conf,
			Url:  u,
			User: m.Author,
		}

		err = suggestion.Validate()
		if err != nil {
			return err
		}

		err = suggestion.Post(s)
		return err
	}
	return nil
}
