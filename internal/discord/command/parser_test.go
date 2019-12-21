package command

import (
	"reflect"
	"testing"

	"github.com/stretchr/testify/assert"
)

var (
	testCmd = &Command{
		Name: "test",
		Args: []*Argument{
			{
				Name: "one",
				Type: reflect.TypeOf(1),
			},
			{
				Name:   "two",
				Type:   reflect.TypeOf(""),
				Concat: true,
			},
		},
		Run: nil,
	}
)

func TestNewParser(t *testing.T) {
	p := NewParser(NewRegistry())
	exp := &Parser{NewRegistry()}
	assert.Equal(t, exp, p)
}

func TestParser_ParseNotEnoughArgs(t *testing.T) {
	p := NewParser(registry())
	res, err := p.Parse(testCmd.Name, "1")
	assert.Error(t, err)
	assert.Nil(t, res)
}

func TestParser_ParseValidArgs(t *testing.T) {
	p := NewParser(registry())
	res, err := p.Parse(testCmd.Name, "1 two")
	exp := map[string]interface{}{
		"one": 1,
		"two": "two",
	}
	assert.NoError(t, err)
	assert.Equal(t, exp, res)
}

func TestParser_ParseInvalidArgs(t *testing.T) {
	p := NewParser(registry())
	res, err := p.Parse(testCmd.Name, "one two")
	assert.Error(t, err)
	assert.Nil(t, res)
}

func TestParser_ParseConcat(t *testing.T) {
	p := NewParser(registry())
	res, err := p.Parse(testCmd.Name, "1 two is a long concat argument")
	exp := map[string]interface{}{
		"one": 1,
		"two": "two is a long concat argument",
	}
	assert.NoError(t, err)
	assert.Equal(t, exp, res)
}

func TestParser_ParseUnknownCommand(t *testing.T) {
	p := NewParser(NewRegistry())
	res, err := p.Parse(testCmd.Name, "one two")
	assert.Error(t, err)
	assert.Nil(t, res)
}

func registry() *Registry {
	reg := NewRegistry()
	reg.Commands[testCmd.Name] = testCmd
	return reg
}
