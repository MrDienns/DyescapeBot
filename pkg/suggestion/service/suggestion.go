package service

import (
	"fmt"
	"net/url"

	"github.com/bwmarrin/discordgo"
)

// Suggestion struct is used to track data of a created suggestion.
type Suggestion struct {
	Conf *SuggestionConfiguration
	Url  *url.URL
	User *discordgo.User
}

// Validate will validate the suggestion data and return an error if anything is wrong.
func (s *Suggestion) Validate() error {
	if !s.hasHost(s.Url.Hostname(), s.Conf.SuggestionWebsites) {
		return fmt.Errorf("please send a %s URL pointing to your suggestion",
			s.Conf.SuggestionWebsites)
	}

	return nil
}

func (s *Suggestion) hasHost(host string, allowed []string) bool {
	for _, site := range allowed {
		if site == host {
			return true
		}
	}
	return false
}

// Post takes the data of the *Suggestion struct and posts a message in the configured channel. Configured reaction
// emojis are added so that members can vote on the suggestions.
func (s *Suggestion) Post(serv *Service) error {
	mess, err := serv.Session.ChannelMessageSend(s.Conf.SuggestionChannelID, fmt.Sprintf("Suggested by %s:\n%s",
		s.User.Mention(), s.Url))
	if err != nil {
		return err
	}

	serv.Session.MessageReactionAdd(mess.ChannelID, mess.ID, s.Conf.AgreeEmojiID)
	serv.Session.MessageReactionAdd(mess.ChannelID, mess.ID, s.Conf.NeutralEmojiID)
	serv.Session.MessageReactionAdd(mess.ChannelID, mess.ID, s.Conf.DisagreeEmojiID)
	return nil
}
