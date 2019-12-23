package arguments

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestDiscordUserResolver_parseUserString(t *testing.T) {
	exp := "267965217412087818"
	act := parseUserString("<@!267965217412087818>")
	assert.Equal(t, exp, act)
}
