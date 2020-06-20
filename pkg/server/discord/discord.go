package discord

import (
	"github.com/Dyescape/DyescapeBot/pkg/server"
	"github.com/bwmarrin/discordgo"
)

// Discord represents a Discord server implementation.
type Discord struct {
	Session  *discordgo.Session
	Services []server.Service
	token    string
}

// NewDiscord takes a token and slice of services to initialise on this server.
func NewDiscord(token string) *Discord {
	return &Discord{token: token}
}

// Start launches the Discord bot server.
func (d *Discord) Start() error {
	sess, err := discordgo.New(d.token)
	if err != nil {
		return err
	}
	d.Session = sess
	err = sess.Open()
	if err != nil {
		return err
	}
	return d.initialiseServices()
}

// RegisterService takes a service argument and registers it on the server.
func (d *Discord) RegisterService(service server.Service) {
	d.Services = append(d.Services, service)
}

// initialiseServices iterates over the services and initialises all of them. If any returned an error, the process is
// aborted and the error is returend.
func (d *Discord) initialiseServices() error {
	for _, serv := range d.Services {
		if err := serv.Initialise(); err != nil {
			return err
		}
	}
	return nil
}
