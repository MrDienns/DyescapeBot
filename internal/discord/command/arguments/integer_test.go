package arguments

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestIntegerResolver_ResolveValidValue(t *testing.T) {
	ir := &IntegerResolver{}
	val, err := ir.Resolve("1")
	assert.NoError(t, err)
	assert.Equal(t, 1, val)
}

func TestIntegerResolver_ResolveInvalidValue(t *testing.T) {
	ir := &IntegerResolver{}
	val, err := ir.Resolve("1asdf")
	assert.Error(t, err)
	assert.Nil(t, val)
}
