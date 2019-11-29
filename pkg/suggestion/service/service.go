package service

import (
	"encoding/json"
	"net/url"

	"github.com/Dyescape/DyescapeBot/pkg/command/handler"

	"github.com/Dyescape/DyescapeBot/internal/app/log"

	"github.com/Dyescape/DyescapeBot/pkg/command/service"

	"github.com/ThreeDotsLabs/watermill"
	"github.com/ThreeDotsLabs/watermill/message"

	"github.com/ThreeDotsLabs/watermill-kafka/pkg/kafka"

	"github.com/Dyescape/DyescapeBot/internal/app/configuration"

	"github.com/Dyescape/DyescapeBot/internal/app/discord"

	"github.com/bwmarrin/discordgo"
)

var (
	marshaler = kafka.DefaultMarshaler{}
)

type SuggestionService struct {
	*discord.Service
	kafkaConfig  *service.KafkaConfig
	logger       *log.WatermillLogger
	configReader configuration.ConfigReader
	publisher    message.Publisher
	subscriber   message.Subscriber
	cmdhandler   *handler.Handler
}

func NewSuggestionService(s *discord.Service, config *service.KafkaConfig, l *log.WatermillLogger, c configuration.ConfigReader) *SuggestionService {
	return &SuggestionService{
		Service:      s,
		kafkaConfig:  config,
		logger:       l,
		configReader: c,
		cmdhandler: &handler.Handler{
			Service:   s,
			Logger:    l.Logger,
			Executors: make(map[string]handler.Executor, 0),
			Topic:     config.CommandCalledTopic,
		},
	}
}

func (s *SuggestionService) Start() error {
	publisher, err := kafka.NewPublisher(s.kafkaConfig.Brokers, marshaler, nil, s.logger)
	if err != nil {
		return err
	}
	s.publisher = publisher
	err = s.registerCommands()
	if err != nil {
		return err
	}

	subscriber, err := kafka.NewSubscriber(kafka.SubscriberConfig{
		Brokers:       s.kafkaConfig.Brokers,
		ConsumerGroup: s.kafkaConfig.CommandFetchTopic,
	}, nil, marshaler, s.logger)
	if err != nil {
		return err
	}
	s.subscriber = subscriber
	s.cmdhandler.Subscriber = subscriber

	s.cmdhandler.RegisterCommand("suggest", s.CmdSuggest)
	return nil
}

func (s *SuggestionService) CmdSuggest(e *service.CommandCalledEvent) error {
	return nil
}

func (s *SuggestionService) registerCommands() error {
	registeredEvent := &service.CommandRegisteredEvent{
		Module:    "Suggestion",
		Command:   "suggest",
		Arguments: "<URL>",
	}
	payload, err := json.Marshal(registeredEvent)
	if err != nil {
		return err
	}
	err = s.publisher.Publish(s.kafkaConfig.CommandRegisteredTopic, &message.Message{
		UUID:    watermill.NewUUID(),
		Payload: payload,
	})
	return err
}

// suggest is the command handling function for the suggestion command.
func (s *SuggestionService) suggest(sess *discordgo.Session, m *discordgo.MessageCreate, args map[string]interface{}) error {
	conf, err := Configuration(s.configReader, m.GuildID)
	if err != nil {
		return err
	}
	urlval := args["url"]
	if u, ok := urlval.(*url.URL); ok {
		suggestion := &Suggestion{
			Conf: conf,
			Url:  u,
			User: m.Author,
		}

		err = suggestion.Validate()
		if err != nil {
			return err
		}

		err = suggestion.Post(s)
		return err
	}
	return nil
}
