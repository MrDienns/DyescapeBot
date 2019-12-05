package command

import (
	"github.com/stretchr/testify/assert"
	"reflect"
	"testing"
)

func TestNewRegistry(t *testing.T) {
	reg := NewRegistry()
	exp := &Registry{
		Resolvers: map[reflect.Type]Resolver{
			reflect.TypeOf(0):  IntegerResolver{},
			reflect.TypeOf(""): StringResolver{},
		},
		Commands: map[string]*Command{},
	}
	assert.Equal(t, exp, reg)
}
