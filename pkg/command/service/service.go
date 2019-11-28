package service

import (
	"context"
	"encoding/json"
	"fmt"
	"strings"

	"github.com/Dyescape/DyescapeBot/internal/app/log"

	"github.com/ThreeDotsLabs/watermill/message/router/middleware"
	"github.com/ThreeDotsLabs/watermill/message/router/plugin"

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
	publisher       message.Publisher
	subscriber      message.Subscriber
	brokers         []string
	calledtopic     string
	registeredtopic string
	fetchtopic      string
	registry        registry
}

// NewCommandService creates a new *commandService struct.
func NewCommandService(serv *discord.Service, brokers []string, calledtopic, registeredtopic, fetchtopic string) *commandService {
	return &commandService{
		Service:         serv,
		brokers:         brokers,
		calledtopic:     calledtopic,
		registeredtopic: registeredtopic,
		fetchtopic:      fetchtopic,
		registry:        registry{Commands: map[string]*command{}},
	}
}

// Start will start the service. It will setup a Discord message handler and a Kafka publishing connection.
func (cs *commandService) Start() error {

	logger := log.NewWatermillLogger()

	publisher, err := kafka.NewPublisher(cs.brokers, marshaler, nil, logger)
	if err != nil {
		return err
	}
	cs.publisher = publisher

	subscriber, err := kafka.NewSubscriber(kafka.SubscriberConfig{
		Brokers:       cs.brokers,
		ConsumerGroup: cs.registeredtopic,
	}, nil, marshaler, logger)
	if err != nil {
		return err
	}
	cs.subscriber = subscriber

	router, err := message.NewRouter(message.RouterConfig{}, logger)
	if err != nil {
		panic(err)
	}

	router.AddPlugin(plugin.SignalsHandler)
	router.AddMiddleware(middleware.Recoverer)

	cs.Session.AddHandler(cs.ReadMessage)

	// Adding a handler (multiple handlers can be added)
	router.AddHandler(
		"command_module",   // handler name, must be unique
		cs.registeredtopic, // topic from which messages should be consumed
		subscriber,
		cs.calledtopic, // topic to which messages should be published
		publisher,
		func(msg *message.Message) ([]*message.Message, error) {
			event := &CommandRegisteredEvent{}
			err := json.Unmarshal(msg.Payload, event)
			if err != nil {
				// When a handler returns an error, the default behavior is to send a Nack (negative-acknowledgement).
				// The message will be processed again.
				//
				// You can change the default behaviour by using middlewares, like Retry or PoisonQueue.
				// You can also implement your own middleware.
				return nil, err
			}

			cs.registry.Commands[event.Command] = &command{
				Module:    event.Module,
				Name:      event.Command,
				Arguments: event.Arguments,
			}
			// TODO: Log
			return []*message.Message{}, nil
		},
	)

	fetchEvent := &CommandFetchEvent{}
	payload, err := json.Marshal(fetchEvent)
	if err != nil {
		return err
	}
	err = cs.publisher.Publish(cs.fetchtopic, &message.Message{
		UUID:    watermill.NewUUID(),
		Payload: payload,
	})
	if err != nil {
		return err
	}

	if err := router.Run(context.Background()); err != nil {
		panic(err)
	}
	return nil
}

// ReadMessage will consume all sent messages from Discord. It will try to parse the message to a command. If
// successful, the command is transformed into a CommandCalledEvent and a payload is published on Apache Kafka.
func (cs *commandService) ReadMessage(s *discordgo.Session, m *discordgo.MessageCreate) {
	if !cs.isCommand(m) {
		return
	}

	if strings.HasPrefix(m.Content, "!! help") {
		cmds := ""
		for _, command := range cs.registry.Commands {
			cmds = cmds + fmt.Sprintf("%v", command.Name)
		}
		cs.Service.SendEmbed(m.ChannelID, "**Command overview**", cmds, "Command module")
		return
	}

	event := asCommandEvent(m)
	payload, err := json.Marshal(event)
	if err != nil {
		// TODO: Logger
		cs.Service.SendEmbed(m.ChannelID, "**Error**", "Failed to process command.", "Command module")
		fmt.Println(err.Error())
	}

	err = cs.publisher.Publish(cs.calledtopic, &message.Message{
		UUID:    watermill.NewUUID(),
		Payload: payload,
	})
	if err != nil {
		cs.Service.SendEmbed(m.ChannelID, "**Error**", "Failed to process command.", "Command module")
	} else {
		cs.Service.SendEmbed(m.ChannelID, "**Created command**",
			"Successfully created and published the command.", "Command module")
	}
}

// isCommand checks if the message is meant to be a command.
func (cs *commandService) isCommand(m *discordgo.MessageCreate) bool {
	// TODO: Dynamic prefix
	return strings.HasPrefix(m.Content, "!!")
}

// asCommandEvent will take a *discordgo.MessageCreate struct and transforms it into a *CommandCalledEvent
// struct which represents the created command.
func asCommandEvent(m *discordgo.MessageCreate) *CommandCalledEvent {
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
	m = strings.TrimPrefix(m, "!! test")
	return strings.TrimSpace(m)
}
