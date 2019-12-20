package command

import (
	"net/url"
	"reflect"
	"testing"

	"github.com/Dyescape/DyescapeBot/internal/discord/command/arguments"
	"github.com/stretchr/testify/assert"
)

func TestNewRegistry(t *testing.T) {
	reg := NewRegistry()
	exp := &Registry{
		Resolvers: map[reflect.Type]Resolver{
			reflect.TypeOf(0):         arguments.IntegerResolver{},
			reflect.TypeOf(""):        arguments.StringResolver{},
			reflect.TypeOf(url.URL{}): arguments.UrlResolver{},
		},
		Commands: map[string]*Command{},
	}
	assert.Equal(t, exp, reg)
}
