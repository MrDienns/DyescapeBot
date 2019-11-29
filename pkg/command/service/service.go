package service

import (
	"context"
	"encoding/json"
	"fmt"
	"sort"
	"strings"

	"github.com/Dyescape/DyescapeBot/internal/app/log"

	"github.com/ThreeDotsLabs/watermill/message"

	"github.com/Dyescape/DyescapeBot/internal/app/discord"
	"github.com/ThreeDotsLabs/watermill"
	"github.com/ThreeDotsLabs/watermill-kafka/pkg/kafka"
	"github.com/bwmarrin/discordgo"
)

var (
	marshaler = kafka.DefaultMarshaler{}
)

// commandService extends *discord.Service by adding a publisher field, slice of brokers and the topic to publish the
// CommandCalledEvent on.
type commandService struct {
	*discord.Service
	kafkaConfig *KafkaConfig
	logger      *log.WatermillLogger
	registry    *registry
	publisher   message.Publisher
	subscriber  message.Subscriber
}

// NewCommandService creates a new *commandService struct.
func NewCommandService(serv *discord.Service, config *KafkaConfig, logger *log.WatermillLogger) *commandService {
	return &commandService{
		Service:     serv,
		kafkaConfig: config,
		logger:      logger,
		registry:    &registry{Commands: map[string]*command{}},
	}
}

// Start will start the service. It will setup a Discord message handler and a Kafka publishing connection.
func (cs *commandService) Start() error {

	publisher, err := kafka.NewPublisher(cs.kafkaConfig.Brokers, marshaler, nil, cs.logger)
	if err != nil {
		return err
	}
	cs.publisher = publisher

	subscriber, err := kafka.NewSubscriber(kafka.SubscriberConfig{
		Brokers:       cs.kafkaConfig.Brokers,
		ConsumerGroup: cs.kafkaConfig.CommandRegisteredTopic,
	}, nil, marshaler, cs.logger)
	if err != nil {
		return err
	}
	cs.subscriber = subscriber
	registered, err := cs.subscriber.Subscribe(context.Background(), cs.kafkaConfig.CommandRegisteredTopic)
	if err != nil {
		cs.logger.Logger.Error(err.Error())
		return err
	}
	go cs.RegisterCommand(registered)

	cs.Session.AddHandler(cs.ReadMessage)

	fetchEvent := &CommandFetchEvent{}
	payload, err := json.Marshal(fetchEvent)
	if err != nil {
		return err
	}
	err = cs.publisher.Publish(cs.kafkaConfig.CommandFetchTopic, &message.Message{
		UUID:    watermill.NewUUID(),
		Payload: payload,
	})
	if err != nil {
		return err
	}
	return nil
}

// RegisterCommand consumes a channel to register the commands internally.
func (cs *commandService) RegisterCommand(cmds <-chan *message.Message) {
	for cmd := range cmds {
		event := &CommandRegisteredEvent{}
		err := json.Unmarshal(cmd.Payload, event)
		if err != nil {
			cs.logger.Logger.Error(err.Error())
		}

		cs.registry.Commands[event.Command] = &command{
			Module:    event.Module,
			Name:      event.Command,
			Arguments: event.Arguments,
		}
		cs.logger.Logger.Info(fmt.Sprintf("Registered command '%s'", event.Command))
	}
}

// ReadMessage will consume all sent messages from Discord. It will try to parse the message to a command. If
// successful, the command is transformed into a CommandCalledEvent and a payload is published on Apache Kafka.
func (cs *commandService) ReadMessage(s *discordgo.Session, m *discordgo.MessageCreate) {
	if !cs.isCommand(m) {
		return
	}

	if strings.HasPrefix(m.Content, "!! help") {
		cmdsPerModule := cs.commandsPerModule()

		help := "Below is an overview of all of the commands that you have permission to.\n"
		for module, cmds := range cmdsPerModule {
			help = help + fmt.Sprintf("\n**%v**\n", module)
			for _, cmd := range cmds {
				var args string
				if cs.registry.Commands[cmd].Arguments != "" {
					args = " " + cs.registry.Commands[cmd].Arguments
				}
				help = help + fmt.Sprintf("　%v%v\n", cmd, args)
			}
		}
		cs.Service.SendEmbed(m.ChannelID, "Command overview", help, "Command module")
		return
	}

	event := asCommandEvent(m)
	payload, err := json.Marshal(event)
	if err != nil {
		cs.Service.SendEmbed(m.ChannelID, "Error", "Failed to process command.", "Command module")
		cs.logger.Logger.Error(err.Error())
	}

	err = cs.publisher.Publish(cs.kafkaConfig.CommandCalledTopic, &message.Message{
		UUID:    watermill.NewUUID(),
		Payload: payload,
	})
	if err != nil {
		cs.Service.SendEmbed(m.ChannelID, "Error", "Failed to process command.", "Command module")
	} else {
		cs.Service.SendEmbed(m.ChannelID, "Created command",
			"Successfully created and published the command.", "Command module")
	}
}

// isCommand checks if the message is meant to be a command.
func (cs *commandService) isCommand(m *discordgo.MessageCreate) bool {
	// TODO: Dynamic prefix
	return strings.HasPrefix(m.Content, "!!")
}

// commandsPerModule returns a map with a string - the command module - as key and a slice of strings as value. The
// value represents the command names. They're sorted alphabetically.
func (cs *commandService) commandsPerModule() map[string][]string {
	ret := make(map[string][]string, 0)
	for _, command := range cs.registry.Commands {
		if mod, ok := ret[command.Module]; ok {
			mod = append(mod, command.Name)
		} else {
			ret[command.Module] = []string{command.Name}
		}
	}

	for _, cmds := range ret {
		sort.Strings(cmds)
	}

	return ret
}

// asCommandEvent will take a *discordgo.MessageCreate struct and transforms it into a *CommandCalledEvent
// struct which represents the created command.
func asCommandEvent(m *discordgo.MessageCreate) *CommandCalledEvent {
	// TODO: Dynamic prefix
	return &CommandCalledEvent{
		User:    m.Author.ID,
		Channel: m.ChannelID,
		Guild:   m.GuildID,
		Command: commandFromMessage(m.Content),
	}
}

// commandFromMessage takes a string and trims the prefix from it.
func commandFromMessage(m string) string {
	// TODO: Dynamic prefix
	m = strings.TrimPrefix(m, "!!")
	return strings.TrimSpace(m)
}
