package message

import (
	"github.com/Dyescape/DyescapeBot/pkg/model"
	"github.com/Dyescape/DyescapeBot/pkg/server"
)

// Message is an interface used to define an abstract service module for consuming messages sent by users.
type Message interface {
	server.Service
}

// Handle is an abstract function which can be used by the domain logic to consume messages.
type Handle func(user *model.User, message string) *Response
