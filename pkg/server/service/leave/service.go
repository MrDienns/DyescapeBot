package leave

import (
	"github.com/Dyescape/DyescapeBot/pkg/model"
	"github.com/Dyescape/DyescapeBot/pkg/server"
)

// Leave is an interface which defines an abstract service which listens for users leaving the server.
type Leave interface {
	server.Service
}

// Handle is the handle function for when a server leaves a server. The function is invoked when the user leaves.
type Handle func(user *model.User, server string)
