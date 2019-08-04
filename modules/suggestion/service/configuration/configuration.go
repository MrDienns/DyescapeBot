package configuration

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
)

// SuggestionConfiguration struct is a struct used for the configuration of
// the suggestion service. It contains configuration values necessairy to
// properly offer the suggestion service.
type SuggestionConfiguration struct {
	SuggestionChannelID string `json:"suggestionChannelId"`
	SuggestionWebsite   string `json:"suggestionWebsite"`
	AgreeEmojiID        string `json:"agreeEmojiId"`
	NeutralEmojiID      string `json:"neutralEmojiId"`
	DisagreeEmojiID     string `json:"disagreeEmojiId"`
}

// ConfigReader is a simple struct used as intermediate layer for
// loading configuration files for Guilds. The folderPath property is
// used to indicate in what folder the Guild configurations are located.
type ConfigReader struct {
	folderPath string
}

// NewConfigReader constructs a new ConfigReader struct with
// the passed folder path for the guild configurations.
func NewConfigReader(folderPath string) *ConfigReader {
	return &ConfigReader{folderPath}
}

// IsValid checks if all configuration values have a value. If so, true is
// returned. If any value is empty, false is returned.
func (s *SuggestionConfiguration) IsValid() bool {
	return s.SuggestionChannelID != "" && s.SuggestionWebsite != "" &&
		s.AgreeEmojiID != "" && s.NeutralEmojiID != "" && s.DisagreeEmojiID != ""
}

// ReadConfiguration will read and return the SuggestionConfiguration struct
// that belongs to the provided Guild.
func (r *ConfigReader) ReadConfiguration(guildID string) *SuggestionConfiguration {
	b, err := ioutil.ReadFile(fmt.Sprintf("%s/%s.json", r.folderPath, guildID))
	if err != nil {
		return &SuggestionConfiguration{}
	}
	var conf SuggestionConfiguration
	json.Unmarshal(b, &conf)
	return &conf
}
