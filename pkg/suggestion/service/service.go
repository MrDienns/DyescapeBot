package service

import (
	"context"
	"net/url"

	"github.com/Dyescape/DyescapeBot/internal/discord/command"

	"github.com/Dyescape/DyescapeBot/internal/app/log"

	"github.com/Dyescape/DyescapeBot/pkg/command/handler"
	"github.com/Dyescape/DyescapeBot/pkg/command/service"

	"github.com/ThreeDotsLabs/watermill/message"

	"github.com/ThreeDotsLabs/watermill-kafka/pkg/kafka"

	"github.com/Dyescape/DyescapeBot/internal/configuration"

	"github.com/Dyescape/DyescapeBot/internal/discord"
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

	reg := command.NewRegistry()
	parser := command.NewParser(reg)

	return &SuggestionService{
		Service:      s,
		kafkaConfig:  config,
		logger:       l,
		configReader: c,
		cmdhandler: &handler.Handler{
			Service:       s,
			Logger:        l.Logger,
			Registry:      reg,
			Parser:        parser,
			RegisterTopic: config.CommandRegisterTopic,
			CallTopic:     config.CommandCallTopic,
		},
	}
}

// Start function will start the service by building the relevant Kafka subscribers and publishers. It will start
// consuming and publishing events to communicate to the other modules.
func (s *SuggestionService) Start() error {
	marshaler := kafka.DefaultMarshaler{}
	publisher, err := kafka.NewPublisher(s.kafkaConfig.Brokers, marshaler, nil, s.logger)
	if err != nil {
		return err
	}
	s.publisher = publisher

	subscriber, err := kafka.NewSubscriber(kafka.SubscriberConfig{
		Brokers:       s.kafkaConfig.Brokers,
		ConsumerGroup: "suggestion",
	}, nil, marshaler, s.logger)
	if err != nil {
		return err
	}
	s.subscriber = subscriber
	s.cmdhandler.Subscriber = subscriber
	s.cmdhandler.Publisher = publisher

	if err := s.subscribeCommandFetch(); err != nil {
		return err
	}

	s.cmdhandler.Start()

	s.registerCommands()
	return nil
}

func (s *SuggestionService) registerCommands() {
	suggestCmd.Run = s.suggest
	s.cmdhandler.RegisterCommand(suggestCmd)
}

// subscribeCommandFetch will uses the listener and subscribe to the configured topic. A new goroutine is started to
// consume the events.
func (s *SuggestionService) subscribeCommandFetch() error {
	fetchChan, err := s.subscriber.Subscribe(context.Background(), s.kafkaConfig.CommandFetchTopic)
	if err != nil {
		s.Logger.Error(err.Error())
		return err
	}
	go s.consumeCommandFetch(fetchChan)
	return nil
}

// consumeCommandFetch is a function which has a channel as parameter, used to receive the command fetch events
// forwarded by the Kafka consumer.
func (s *SuggestionService) consumeCommandFetch(events <-chan *message.Message) {
	for e := range events {
		e.Ack()
		s.registerCommands()
	}
}

// suggest is the command handling function for the suggestion command.
func (s *SuggestionService) suggest(service *discord.Service, e *service.CommandCalledEvent, args map[string]interface{}) error {
	conf, err := Configuration(s.configReader, e.Guild)
	if err != nil {
		return err
	}
	urlval := args["url"]
	if u, ok := urlval.(*url.URL); ok {
		user, _ := service.Session.User(e.User)
		suggestion := &Suggestion{
			Conf: conf,
			Url:  u,
			User: user,
		}

		err = suggestion.Validate()
		if err != nil {
			return err
		}

		err = suggestion.Post(s.Session)
		return err
	}
	return nil
}
