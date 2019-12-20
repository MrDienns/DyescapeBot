package handler

import (
	"context"
	"encoding/json"
	"fmt"
	"strings"

	"github.com/Dyescape/DyescapeBot/internal/discord/command"

	"github.com/ThreeDotsLabs/watermill"

	"github.com/Dyescape/DyescapeBot/internal/discord"

	"github.com/Dyescape/DyescapeBot/internal/app/log"

	"github.com/Dyescape/DyescapeBot/pkg/command/service"
	"github.com/ThreeDotsLabs/watermill/message"
)

// Handler struct consists of a *Discord.Service, a *log.Logger, an executor map and a consuming topic. It is
// responsible for being the communication between Kafka and a command handling Go function.
type Handler struct {
	Service       *discord.Service
	Logger        *log.Logger
	Subscriber    message.Subscriber
	Publisher     message.Publisher
	Registry      *command.Registry
	Parser        *command.Parser
	RegisterTopic string
	CallTopic     string
}

// Executor is a function designed to handle a command.
type Executor func(e *service.CommandCalledEvent, args map[string]interface{}) error

// Handle will accept the Go channel that is communicated from Watermill in order to consume Kafka events.
func (h *Handler) Handle(msgs <-chan *message.Message) {
	for msg := range msgs {
		msg.Ack()
		var event service.CommandCalledEvent
		err := json.Unmarshal(msg.Payload, &event)
		if err != nil {
			h.Logger.Error(err.Error())
		}
		cmdName := strings.Split(strings.TrimSpace(event.Command), " ")[0]
		args := strings.TrimLeft(event.Command, strings.TrimSpace(cmdName))
		args = strings.TrimSpace(args)
		if cmd, ok := h.Registry.Commands[cmdName]; ok {
			parsedArgs, err := h.Parser.Parse(cmdName, args)
			if err != nil {
				h.Service.SendEmbed(event.Channel, "Error parsing arguments", err.Error(), h.Service.Module)
				continue
			}
			err = cmd.Run(h.Service, &event, parsedArgs)
			if err != nil {
				h.Service.SendEmbed(event.Channel, "Error executing command", err.Error(), h.Service.Module)
			}
		}
	}
}

func (h *Handler) Start() {
	called, err := h.Subscriber.Subscribe(context.Background(), h.CallTopic)
	if err != nil {
		h.Logger.Error(err.Error())
	}
	go h.Handle(called)
}

// RegisterCommand takes the command string and an executor function and it will link them together through Kafka.
func (h *Handler) RegisterCommand(cmd *command.Command) error {
	h.Registry.Commands[cmd.Name] = cmd
	payload, err := h.commandMessage(cmd.Name, cmd.Usage)
	if err != nil {
		return err
	}
	return h.Publisher.Publish(h.RegisterTopic, payload)
}

// commandMessage takes the command and arg string and turns it into a payload for Kafka.
func (h *Handler) commandMessage(cmd, args string) (*message.Message, error) {
	event := &service.CommandRegisteredEvent{
		Module:    h.Service.Module,
		Command:   cmd,
		Arguments: args,
	}
	payload, err := json.Marshal(event)
	if err != nil {
		h.Logger.Error(fmt.Sprintf("failed to prepare command register payload: %v", err))
		return nil, err
	}
	return &message.Message{UUID: watermill.NewUUID(), Payload: payload}, nil
}
