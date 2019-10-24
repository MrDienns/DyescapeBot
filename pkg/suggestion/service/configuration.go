package service

import (
	"fmt"

	config "github.com/Dyescape/DyescapeBot/internal/app/configuration"
)

// SuggestionConfiguration struct is a struct used for the configuration of
// the suggestion service. It contains configuration values necessairy to
// properly offer the suggestion service.
type SuggestionConfiguration struct {
	SuggestionChannelID string   `json:"suggestionChannelId"`
	SuggestionWebsites  []string `json:"suggestionWebsites"`
	AgreeEmojiID        string   `json:"agreeEmojiId"`
	NeutralEmojiID      string   `json:"neutralEmojiId"`
	DisagreeEmojiID     string   `json:"disagreeEmojiId"`
}

// Configuration will return the *SuggestionConfiguration struct if the guild has a valid configuration. If the
// configuration does not exist, or it exists but it's not valid, an error is returned.
func Configuration(cf config.ConfigReader, guildID string) (*SuggestionConfiguration, error) {
	conf := &SuggestionConfiguration{}
	err := cf.ReadConfiguration(guildID, conf)
	if err != nil {
		return nil, fmt.Errorf("the suggestion service is not configured")
	}
	if !conf.IsValid() {
		return nil, fmt.Errorf("the suggestion service is not fully configured")
	}
	return conf, nil
}

// IsValid checks if all configuration values have a value. If so, true is
// returned. If any value is empty, false is returned.
func (s *SuggestionConfiguration) IsValid() bool {
	return s.SuggestionChannelID != "" && len(s.SuggestionWebsites) != 0 &&
		s.AgreeEmojiID != "" && s.NeutralEmojiID != "" && s.DisagreeEmojiID != ""
}
