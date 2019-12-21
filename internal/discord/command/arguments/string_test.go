package arguments

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestStringResolver_Resolve(t *testing.T) {
	sr := &StringResolver{}
	val, err := sr.Resolve("test")
	assert.NoError(t, err)
	assert.Equal(t, "test", val)
}
