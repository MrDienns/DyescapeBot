package command

import (
	library "github.com/Dyescape/DyescapeBot/internal/app/configuration"
	"github.com/bwmarrin/discordgo"
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestNewHandler(t *testing.T) {
	h := handler()
	reader := library.NewFlatFileConfigReader("")
	reg := NewRegistry()
	exp := &Handler{
		ConfigReader:         reader,
		CommandConfiguration: NewConfiguration(reader),
		Registry:             reg,
		Parser:               NewParser(reg),
	}
	assert.Equal(t, exp, h)
}

func TestHandler_HandleNoCommand(t *testing.T) {
	h := handler()
	m := &discordgo.MessageCreate{
		Message: &discordgo.Message{
			Content: "hello",
		},
	}
	h.Handle(nil, m)
}

func handler() *Handler {
	reader := library.NewFlatFileConfigReader("")
	reg := NewRegistry()
	return NewHandler(reader, NewConfiguration(reader), reg, NewParser(reg))
}

