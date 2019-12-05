package command

import (
	"github.com/stretchr/testify/assert"
	"reflect"
	"testing"
)

var (
	testCmd = &Command{
		Name: "test",
		Args: map[string]reflect.Type{
			"one": reflect.TypeOf(""),
			"two": reflect.TypeOf(1),
		},
		Run:  nil,
	}
)

func TestNewParser(t *testing.T) {
	p := NewParser(NewRegistry())
	exp := &Parser{NewRegistry()}
	assert.Equal(t, exp, p)
}

func TestParser_ParseNotEnoughArgs(t *testing.T) {
	p := NewParser(NewRegistry())
	res, err := p.Parse(testCmd, []string{"one"})
	assert.Error(t, err)
	assert.Nil(t, res)
}

func TestParser_ParseValidArgs(t *testing.T) {
	p := NewParser(NewRegistry())
	res, err := p.Parse(testCmd, []string{"one", "2"})
	exp := map[string]interface{}{
		"one": "one",
		"two": 2,
	}
	assert.NoError(t, err)
	assert.Equal(t, exp, res)
}

func TestParser_ParseInvalidArgs(t *testing.T) {
	p := NewParser(NewRegistry())
	res, err := p.Parse(testCmd, []string{"one", "two"})
	assert.Error(t, err)
	assert.Nil(t, res)
}