package service

import (
	"net/url"
	"reflect"

	"github.com/Dyescape/DyescapeBot/internal/discord/command"
)

var (
	suggestCmd = &command.Command{
		Name: "suggest",
		Args: []*command.Argument{
			0: {
				Name:     "url",
				Type:     reflect.TypeOf(url.URL{}),
				Optional: false,
				Concat:   false,
			},
		},
		Usage: "<url>",
	}
)
