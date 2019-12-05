package command

import (
	"testing"

	library "github.com/Dyescape/DyescapeBot/internal/configuration"
	"github.com/stretchr/testify/assert"
)

func TestNewConfiguration(t *testing.T) {
	reader := library.NewFlatFileConfigReader("")
	conf := NewConfiguration(reader)
	exp := &Configuration{
		Reader:      reader,
		ConfigCache: map[string]*guildConfiguration{},
	}
	assert.Equal(t, exp, conf)
}

func TestConfiguration_ConfigurationCached(t *testing.T) {
	conf := NewConfiguration(library.NewFlatFileConfigReader(""))
	cached := &guildConfiguration{Prefix: "t"}
	conf.ConfigCache["testGuild"] = cached
	act := conf.Configuration("testGuild")
	assert.Equal(t, cached, act)
}

func TestConfiguration_ConfigurationRead(t *testing.T) {
	conf := NewConfiguration(library.NewFlatFileConfigReader("configuration_test"))
	exp := &guildConfiguration{Prefix: "t"}
	act := conf.Configuration("testGuild")
	assert.Equal(t, exp, act)
}
