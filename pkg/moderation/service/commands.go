package service

import (
	"reflect"

	"github.com/Dyescape/DyescapeBot/internal/discord"
	"github.com/Dyescape/DyescapeBot/pkg/command/service"

	"github.com/bwmarrin/discordgo"

	"github.com/Dyescape/DyescapeBot/internal/discord/command"
)

var (
	warnCommand = &command.Command{
		Name: "warn",
		Args: []*command.Argument{
			0: {
				Name:     "user",
				Type:     reflect.TypeOf(discordgo.User{}),
				Optional: false,
				Concat:   false,
			},
			1: {
				Name:     "points",
				Type:     reflect.TypeOf(0),
				Optional: false,
				Concat:   false,
			},
			2: {
				Name:     "reason",
				Type:     reflect.TypeOf(""),
				Optional: true,
				Concat:   true,
			},
		},
		Usage: "<User> <Points> [Reason]",
		Run: func(service *discord.Service, e *service.CommandCalledEvent, args map[string]interface{}) error {
			return nil
		},
	}

	unwarnCommand = &command.Command{
		Name: "unwarn",
		Args: []*command.Argument{
			0: {
				Name:     "user",
				Type:     reflect.TypeOf(discordgo.User{}),
				Optional: false,
				Concat:   false,
			},
			1: {
				Name:     "warning",
				Type:     reflect.TypeOf(0), // TODO
				Optional: false,
				Concat:   false,
			},
		},
		Usage: "<User> <warning>",
		Run: func(service *discord.Service, e *service.CommandCalledEvent, args map[string]interface{}) error {
			return nil
		},
	}

	warnActionCommand = &command.Command{
		Name: "warnaction",
		Args: []*command.Argument{
			0: {
				Name:     "points",
				Type:     reflect.TypeOf(0),
				Optional: false,
				Concat:   false,
			},
			1: {
				Name:     "action",
				Type:     reflect.TypeOf(""), // TODO
				Optional: true,
				Concat:   false,
			},
			3: {
				Name:     "duration",
				Type:     reflect.TypeOf(""),
				Optional: true,
				Concat:   false,
			},
		},
		Usage: "<Points> [Action] [Duration]",
		Run: func(service *discord.Service, e *service.CommandCalledEvent, args map[string]interface{}) error {
			return nil
		},
	}

	muteCommand = &command.Command{
		Name: "mute",
		Args: []*command.Argument{
			0: {
				Name:     "user",
				Type:     reflect.TypeOf(discordgo.User{}),
				Optional: false,
				Concat:   false,
			},
			1: {
				Name:     "duration",
				Type:     reflect.TypeOf(""), // TODO
				Optional: true,
				Concat:   false,
			},
			3: {
				Name:     "reason",
				Type:     reflect.TypeOf(""),
				Optional: true,
				Concat:   true,
			},
		},
		Usage: "<User> [Duration] [Reason]",
		Run: func(service *discord.Service, e *service.CommandCalledEvent, args map[string]interface{}) error {
			return nil
		},
	}

	unmuteCommand = &command.Command{
		Name: "unmute",
		Args: []*command.Argument{
			0: {
				Name:     "user",
				Type:     reflect.TypeOf(discordgo.User{}),
				Optional: false,
				Concat:   false,
			},
		},
		Usage: "<User>",
		Run: func(service *discord.Service, e *service.CommandCalledEvent, args map[string]interface{}) error {
			return nil
		},
	}

	banCommand = &command.Command{
		Name: "ban",
		Args: []*command.Argument{
			0: {
				Name:     "user",
				Type:     reflect.TypeOf(discordgo.User{}),
				Optional: false,
				Concat:   false,
			},
			1: {
				Name:     "duration",
				Type:     reflect.TypeOf(""), // TODO
				Optional: true,
				Concat:   false,
			},
			3: {
				Name:     "reason",
				Type:     reflect.TypeOf(""),
				Optional: true,
				Concat:   true,
			},
		},
		Usage: "<User> [Duration] [Reason]",
		Run: func(service *discord.Service, e *service.CommandCalledEvent, args map[string]interface{}) error {
			return nil
		},
	}

	unbanCommand = &command.Command{
		Name: "unban",
		Args: []*command.Argument{
			0: {
				Name:     "user",
				Type:     reflect.TypeOf(discordgo.User{}),
				Optional: false,
				Concat:   false,
			},
		},
		Usage: "<User>",
		Run: func(service *discord.Service, e *service.CommandCalledEvent, args map[string]interface{}) error {
			return nil
		},
	}
)
