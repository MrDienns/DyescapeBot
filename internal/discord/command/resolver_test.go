package command

import (
	"testing"

	"github.com/Dyescape/DyescapeBot/internal/discord/command/arguments"
	"github.com/stretchr/testify/assert"
)

func TestIntegerResolver_ResolveValidValue(t *testing.T) {
	ir := &arguments.IntegerResolver{}
	val, err := ir.Resolve("1")
	assert.NoError(t, err)
	assert.Equal(t, 1, val)
}

func TestIntegerResolver_ResolveInvalidValue(t *testing.T) {
	ir := &arguments.IntegerResolver{}
	val, err := ir.Resolve("1asdf")
	assert.Error(t, err)
	assert.Nil(t, val)
}

func TestStringResolver_Resolve(t *testing.T) {
	sr := &arguments.StringResolver{}
	val, err := sr.Resolve("test")
	assert.NoError(t, err)
	assert.Equal(t, "test", val)
}
