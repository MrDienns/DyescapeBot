package join

import (
	"github.com/Dyescape/DyescapeBot/pkg/model"
	"github.com/Dyescape/DyescapeBot/pkg/server"
)

// Join is an interface which defines an abstract service which listens for users joining the server.
type Join interface {
	server.Service
}

// Handle is the handle function for when a server joins a server. The function is invoked when the user joins.
type Handle func(user *model.User, server string)
