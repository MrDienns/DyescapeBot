package command

import (
	"fmt"
	"strings"

	config "github.com/Dyescape/DyescapeBot/internal/app/configuration"

	"github.com/bwmarrin/discordgo"
)

// Handler is a general struct used to build a command handler. It contains a reference to a *discordgo.Session
// object, the config.ConfigReader interface, and it has a reference to the Configuration struct of the command
// package.
type Handler struct {
	ConfigReader         config.ConfigReader
	CommandConfiguration *Configuration
	Registry             *Registry
	Parser               *Parser
}

// NewHandler will construct a new *Handler with the given parameters.
func NewHandler(configReader config.ConfigReader, cmdConfig *Configuration,
	registry *Registry, parser *Parser) *Handler {
	return &Handler{configReader, cmdConfig, registry, parser}
}

// Handle will accept a *discordgo.MessageCreate argument, which contains all of the relevant info required
// for the command handler to interpret the sent message and parse it as a command if it is one.
func (h *Handler) Handle(sess *discordgo.Session, m *discordgo.MessageCreate) {

	// Load the prefix, and compare it against our message
	conf := h.CommandConfiguration.Configuration(m.GuildID)
	if !strings.HasPrefix(m.Content, conf.Prefix) {
		return
	}

	h.runCommand(sess, m, conf)
}

// runCommand will take the *discordgo.Session, *discordgo.MessageCreate and the *guildConfiguration to
// fully run the command.
func (h *Handler) runCommand(sess *discordgo.Session, m *discordgo.MessageCreate, conf *guildConfiguration) {
	// Bots may not execute commands
	if m.Author.Bot {
		_, err := sess.ChannelMessageSend(m.ChannelID, "how about no?")
		if err != nil {
			// TODO: Use logging framework
			fmt.Println("Error sending message; " + err.Error())
		}
		return
	}

	// Lookup the command and run it
	cmdString := h.sanitizeCommandString(conf.Prefix, m.Content)
	args := strings.Split(cmdString, " ")
	if cmd, ok := h.Registry.Commands[args[0]]; ok {

		args = args[1:]
		parsedArgs, err := h.Parser.Parse(cmd, args)
		if err != nil {
			// TODO: Use logging framework
			_, err = sess.ChannelMessageSend(m.ChannelID, "Error parsing command; "+err.Error())
			if err != nil {
				// TODO: Use logging framework
				fmt.Println("Error sending message; " + err.Error())
			}
			return
		}

		err = cmd.Run(sess, m, parsedArgs)
		if err != nil {
			_, err = sess.ChannelMessageSend(m.ChannelID, "Error executing command; "+err.Error())
			if err != nil {
				// TODO: Use logging framework
				fmt.Println("Error sending message; " + err.Error())
			}
		}
		return
	}

	// TODO: Handle in microservice-way
	_, err := sess.ChannelMessageSend(m.ChannelID, fmt.Sprintf("Unknown command '%s'", args[0]))
	if err != nil {
		// TODO: Use logging framework
		fmt.Println("Error sending message; " + err.Error())
	}
}

// sanitizeCommandString will take the prefix and command string. It will remove the prefix from the command
// string and remove any excessive spaces.
func (h *Handler) sanitizeCommandString(prefix, cmd string) string {
	cmd = strings.Replace(cmd, prefix, "", 1)
	cmd = strings.Join(strings.Fields(cmd), " ")
	return cmd
}
