package service

import (
	"fmt"
	"net/url"
	"strings"

	"github.com/Dyescape/DyescapeBot/modules/suggestion/service/configuration"

	"github.com/bwmarrin/discordgo"
)

// Service strict is used as a base struct for the Discord connection to the
// gateway. Contains a public discordgo.Session field.
type Service struct {
	token        string
	configReader *configuration.ConfigReader
	Session      *discordgo.Session
}

type suggestion struct {
	url     *url.URL
	user    *discordgo.User
	guild   *discordgo.Guild
	channel *discordgo.Channel
}

// NewService accepts a Discord API token and constructs a new Service struct.
func NewService(token string, configReader *configuration.ConfigReader) *Service {
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
	sess.AddHandler(s.onMessage)
	s.Session = sess
	return s.Session.Open()
}

// onMessage handler is called whenever a user sends a message in any channel.
// We listen to the message and determine whether or not it is a command to
// post a suggestion.
func (s *Service) onMessage(sess *discordgo.Session, m *discordgo.MessageCreate) {
	if !strings.HasPrefix(m.Content, "!! suggest") {
		return
	}

	guild, _ := sess.Guild(m.GuildID)
	conf, err := s.getConfiguration(m.GuildID)
	if err != nil {
		sess.ChannelMessageSend(m.ChannelID, err.Error())
	}

	arg := strings.ReplaceAll(m.Content, "!! suggest", "")
	arg = strings.Trim(arg, " ")
	url, err := url.Parse(arg)
	if err != nil {
		sess.ChannelMessageSend(m.ChannelID, err.Error())
		return
	}

	if url.Hostname() != conf.SuggestionWebsite {
		sess.ChannelMessageSend(m.ChannelID, fmt.Sprintf("Please send a %s url pointing to your suggestion.",
			conf.SuggestionWebsite))
		return
	}

	messageChann, _ := sess.Channel(m.ChannelID)
	sugg := &suggestion{
		url:     url,
		user:    m.Author,
		guild:   guild,
		channel: messageChann,
	}

	err = postSuggestsion(sess, sugg, conf)
	if err != nil {
		sess.ChannelMessageSend(m.ChannelID, err.Error())
	}
}

// getConfiguration will return the configuration.SuggestionConfiguration object if
// the guild has a valid configuration. If the configuration does not exist, or it
// exists but it's not valid, an error is returned.
func (s *Service) getConfiguration(guildID string) (*configuration.SuggestionConfiguration, error) {
	conf := s.configReader.ReadConfiguration(guildID)
	if !conf.IsValid() {
		return nil, fmt.Errorf("The suggestion service is not configured")
	}
	return conf, nil
}

// postSuggestion takes the discordgo.Session, a suggestion object and the configuration.SuggestionConfiguration
// objects and posts the suggestion in the configured channel, including reactions to allow users to vote.
func postSuggestsion(sess *discordgo.Session, sugg *suggestion, conf *configuration.SuggestionConfiguration) error {
	mess, err := sess.ChannelMessageSend(conf.SuggestionChannelID, fmt.Sprintf("Suggested by %s:\n%s", sugg.user.Mention(), sugg.url))
	if err != nil {
		return err
	}

	sess.MessageReactionAdd(mess.ChannelID, mess.ID, conf.AgreeEmojiID)
	sess.MessageReactionAdd(mess.ChannelID, mess.ID, conf.NeutralEmojiID)
	sess.MessageReactionAdd(mess.ChannelID, mess.ID, conf.DisagreeEmojiID)
	return nil
}
