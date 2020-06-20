package discord

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestNewDiscord(t *testing.T) {
	exp := &Discord{token: "my_token"}
	act := NewDiscord("my_token")
	assert.Equal(t, exp, act)
}
