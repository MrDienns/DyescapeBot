package command

import (
	"reflect"

	"github.com/bwmarrin/discordgo"
)

// Command is a struct used to define a simple command.
type Command struct {
	Name string
	Args map[string]reflect.Type
	Run  ExecutorFunc
}

// ExecutorFunc represents a function type and its signature which should be followed when adding command
// handlers for registered commands.
type ExecutorFunc func(sess *discordgo.Session, m *discordgo.MessageCreate, args map[string]interface{}) error
