package command

import (
	"reflect"

	"github.com/Dyescape/DyescapeBot/internal/discord"
	"github.com/Dyescape/DyescapeBot/pkg/command/service"
)

// Command is a struct used to define a simple command.
type Command struct {
	Name  string
	Args  []*Argument
	Usage string
	Run   Executor
}

type Argument struct {
	Name     string
	Type     reflect.Type
	Optional bool
	Concat   bool
}

// Executor is a function designed to handle a command.
type Executor func(service *discord.Service, e *service.CommandCalledEvent, args map[string]interface{}) error
