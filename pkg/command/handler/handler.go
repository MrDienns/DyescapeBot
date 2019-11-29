package handler

import (
	"context"
	"encoding/json"

	"github.com/Dyescape/DyescapeBot/internal/app/discord"

	"github.com/Dyescape/DyescapeBot/internal/app/log"

	"github.com/Dyescape/DyescapeBot/pkg/command/service"
	"github.com/ThreeDotsLabs/watermill/message"
)

// Handler struct consists of a *Discord.Service, a *log.Logger, an executor map and a consuming topic. It is
// responsible for being the communication between Kafka and a command handling Go function.
type Handler struct {
	Service    *discord.Service
	Logger     *log.Logger
	Subscriber message.Subscriber
	Executors  map[string]Executor
	Topic      string
}

// Executor is a function designed to handle a command.
type Executor func(e *service.CommandCalledEvent) error

// Handle will accept the Go channel that is communicated from Watermill in order to consume Kafka events.
func (h *Handler) Handle(cmds <-chan *message.Message) {
	for cmd := range cmds {
		var event service.CommandCalledEvent
		err := json.Unmarshal(cmd.Payload, &event)
		if err != nil {
			h.Logger.Error(err.Error())
		}
		if exec, ok := h.Executors[event.Command]; ok {
			err = exec(&event)
			if err != nil {
				h.Service.SendEmbed(event.Channel, "Error", err.Error(), "Command module")
			}
		}
	}
}

// RegisterCommand takes the command string and an executor function and it will link them together through Kafka.
func (h *Handler) RegisterCommand(cmd string, exec Executor) {
	called, err := h.Subscriber.Subscribe(context.Background(), h.Topic)
	if err != nil {
		h.Logger.Error(err.Error())
	}
	h.Executors[cmd] = exec
	go h.Handle(called)
}
