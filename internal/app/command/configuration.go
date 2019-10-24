package command

import config "github.com/Dyescape/DyescapeBot/internal/app/configuration"

// Configuration struct is used to keep certain options for commands. It has a reference to a ConfigReader and
// it has a guild command configuration cache.
type Configuration struct {
	Reader      config.ConfigReader
	ConfigCache map[string]*guildConfiguration
}

// guildConfiguration struct is a struct used to store guild-wide configuration settings for commands. It
// has values such as the configured prefix.
type guildConfiguration struct {
	Prefix string `json:"prefix"`
}

// NewConfiguration will construct a new *Configuration object. It will contain the passed config reader, and it creates
// an empty map for the guild configuration cache.
func NewConfiguration(reader config.ConfigReader) *Configuration {
	return &Configuration{
		Reader:      reader,
		ConfigCache: make(map[string]*guildConfiguration, 0),
	}
}

// Configuration will return a *guildConfiguration. If it is not yet cached by the Configuration struct, it will be
// loaded and cached. If no configuration was found, the default is returned.
func (cc *Configuration) Configuration(guildID string) *guildConfiguration {
	if config, ok := cc.ConfigCache[guildID]; ok {
		return config
	}
	config := &guildConfiguration{}
	err := cc.Reader.ReadConfiguration(guildID, config)
	if err != nil {
		return &guildConfiguration{"!!"}
	}
	return config
}
