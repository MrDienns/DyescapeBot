package service

import (
	"context"
	"encoding/json"
	"fmt"
	"sort"
	"strings"

	"github.com/Dyescape/DyescapeBot/internal/app/events"

	"github.com/Dyescape/DyescapeBot/internal/app/log"

	"github.com/ThreeDotsLabs/watermill/message"

	"github.com/Dyescape/DyescapeBot/internal/discord"
	"github.com/ThreeDotsLabs/watermill"
	"github.com/ThreeDotsLabs/watermill-kafka/pkg/kafka"
	"github.com/bwmarrin/discordgo"
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

	marshaller := kafka.DefaultMarshaler{}

	publisher, err := kafka.NewPublisher(cs.kafkaConfig.Brokers, marshaller, nil, cs.logger)
	if err != nil {
		return err
	}
	cs.publisher = publisher

	subscriber, err := kafka.NewSubscriber(kafka.SubscriberConfig{
		Brokers:       cs.kafkaConfig.Brokers,
		ConsumerGroup: cs.kafkaConfig.CommandRegisterTopic,
	}, nil, marshaller, cs.logger)
	if err != nil {
		return err
	}
	cs.subscriber = subscriber

	if err := cs.subscribeCommandRegister(); err != nil {
		return err
	}

	cs.Service.Session.AddHandler(cs.readMessage)

	return cs.fetchCommands()
}

// subscribeCommandRegister will open a consumer and subscribe to the command register topic.
func (cs *commandService) subscribeCommandRegister() error {
	registerChan, err := cs.subscriber.Subscribe(context.Background(), cs.kafkaConfig.CommandRegisterTopic)
	if err != nil {
		cs.Logger.Error(err.Error())
		return err
	}
	go cs.consumeCommandRegister(registerChan)

	return nil
}

// fetchCommands will use the publisher to publish a CommandFetchEvent in order to communicate to other modules that
// they should fire their command register events.
func (cs *commandService) fetchCommands() error {
	cs.Logger.Info("Fetching commands...")
	e := &events.CommandFetchEvent{}
	payload, err := json.Marshal(e)
	if err != nil {
		return err
	}
	return cs.publisher.Publish(cs.kafkaConfig.CommandFetchTopic, &message.Message{
		UUID:    watermill.NewUUID(),
		Payload: payload,
	})
}

// RegisterCommand consumes a channel to register the commands internally.
func (cs *commandService) consumeCommandRegister(cmds <-chan *message.Message) {
	for cmd := range cmds {
		cmd.Ack()
		event := &CommandRegisteredEvent{}
		err := json.Unmarshal(cmd.Payload, event)
		if err != nil {
			cs.Logger.Error(err.Error())
			continue
		}

		cs.registry.Commands[event.Command] = &command{event.Module, event.Command, event.Arguments}
		cs.Logger.Info(fmt.Sprintf("Module '%v' registered command '%s'", event.Module, event.Command))
	}
}

// ReadMessage will consume all sent messages from Discord. It will try to parse the message to a command. If
// successful, the command is transformed into a CommandCalledEvent and a payload is published on Apache Kafka.
func (cs *commandService) readMessage(s *discordgo.Session, m *discordgo.MessageCreate) {
	if !isCommand(m.Content) {
		return
	}

	if strings.HasPrefix(m.Content, "!! help") {
		cs.help(m)
		return
	}

	event := asCommandEvent(m)
	payload, err := json.Marshal(event)
	if err != nil {
		cs.Service.SendEmbed(m.ChannelID, "Error", "Failed to process command.", cs.Module)
		cs.Logger.Error(err.Error())
		return
	}

	err = cs.publisher.Publish(cs.kafkaConfig.CommandCallTopic, &message.Message{
		UUID:    watermill.NewUUID(),
		Payload: payload,
	})
	if err != nil {
		cs.Service.SendEmbed(m.ChannelID, "Error", "Failed to process command.", cs.Module)
	}
}

func (cs *commandService) help(m *discordgo.MessageCreate) {
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
	cs.Service.SendEmbed(m.ChannelID, "Command overview", help, cs.Module)
}

// commandsPerModule returns a map with a string - the command module - as key and a slice of strings as value. The
// value represents the command names. They're sorted alphabetically.
func (cs *commandService) commandsPerModule() map[string][]string {
	ret := make(map[string][]string, 0)
	for _, command := range cs.registry.Commands {
		if mod, ok := ret[command.Module]; ok {
			mod = append(mod, command.Name)
			ret[command.Module] = mod
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
	return &CommandCalledEvent{
		User:    m.Author.ID,
		Channel: m.ChannelID,
		Guild:   m.GuildID,
		Command: commandFromMessage(m.Content),
	}
}

// isCommand checks if the message is meant to be a command.
func isCommand(m string) bool {
	// TODO: Dynamic prefix
	return strings.HasPrefix(m, "!!")
}

// commandFromMessage takes a string and trims the prefix from it.
func commandFromMessage(m string) string {
	// TODO: Dynamic prefix
	m = strings.TrimPrefix(m, "!!")
	return strings.TrimSpace(m)
}
