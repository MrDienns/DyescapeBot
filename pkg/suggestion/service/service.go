package service

import (
	"context"
	"encoding/json"
	"net/url"

	"github.com/Dyescape/DyescapeBot/internal/app/log"

	"github.com/ThreeDotsLabs/watermill/message/router/middleware"
	"github.com/ThreeDotsLabs/watermill/message/router/plugin"

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
}

func NewSuggestionService(s *discord.Service, config *service.KafkaConfig, l *log.WatermillLogger, c configuration.ConfigReader) *SuggestionService {
	return &SuggestionService{
		Service:      s,
		kafkaConfig:  config,
		logger:       l,
		configReader: c,
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

	router, err := message.NewRouter(message.RouterConfig{}, s.logger)
	if err != nil {
		panic(err)
	}

	router.AddPlugin(plugin.SignalsHandler)
	router.AddMiddleware(middleware.Recoverer)

	// Adding a handler (multiple handlers can be added)
	router.AddHandler(
		"suggestion_module",             // handler name, must be unique
		s.kafkaConfig.CommandFetchTopic, // topic from which messages should be consumed
		subscriber,
		s.kafkaConfig.CommandCalledTopic, // topic to which messages should be published
		publisher,
		func(msg *message.Message) ([]*message.Message, error) {
			err := s.registerCommands()
			// TODO: Log
			return []*message.Message{}, err
		},
	)

	if err := router.Run(context.Background()); err != nil {
		panic(err)
	}
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
