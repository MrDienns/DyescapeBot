package service

import (
	"context"
	"reflect"

	"github.com/Dyescape/DyescapeBot/internal/discord/command/arguments"
	"github.com/bwmarrin/discordgo"

	"github.com/Dyescape/DyescapeBot/internal/app/log"
	"github.com/Dyescape/DyescapeBot/internal/discord"
	"github.com/Dyescape/DyescapeBot/internal/discord/command"
	"github.com/Dyescape/DyescapeBot/pkg/command/handler"
	"github.com/Dyescape/DyescapeBot/pkg/command/service"
	"github.com/ThreeDotsLabs/watermill-kafka/pkg/kafka"
	"github.com/ThreeDotsLabs/watermill/message"
)

type ModerationService struct {
	*discord.Service
	kafkaConfig *service.KafkaConfig
	logger      *log.WatermillLogger
	publisher   message.Publisher
	subscriber  message.Subscriber
	cmdhandler  *handler.Handler
}

func NewModerationService(s *discord.Service, config *service.KafkaConfig, l *log.WatermillLogger) *ModerationService {

	reg := command.NewRegistry()
	reg.Resolvers[reflect.TypeOf(discordgo.User{})] = arguments.NewDiscordUserResolver(s.Session)
	parser := command.NewParser(reg)

	return &ModerationService{
		Service:     s,
		kafkaConfig: config,
		logger:      l,
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

func (s *ModerationService) Start() error {
	marshaler := kafka.DefaultMarshaler{}
	publisher, err := kafka.NewPublisher(s.kafkaConfig.Brokers, marshaler, nil, s.logger)
	if err != nil {
		return err
	}
	s.publisher = publisher

	subscriber, err := kafka.NewSubscriber(kafka.SubscriberConfig{
		Brokers:       s.kafkaConfig.Brokers,
		ConsumerGroup: "moderation",
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

func (s *ModerationService) registerCommands() {
	s.cmdhandler.RegisterCommand(warnCommand)
	s.cmdhandler.RegisterCommand(unwarnCommand)
	s.cmdhandler.RegisterCommand(warnActionCommand)
	s.cmdhandler.RegisterCommand(muteCommand)
	s.cmdhandler.RegisterCommand(unmuteCommand)
	s.cmdhandler.RegisterCommand(banCommand)
	s.cmdhandler.RegisterCommand(unbanCommand)
}

// subscribeCommandFetch will uses the listener and subscribe to the configured topic. A new goroutine is started to
// consume the events.
func (s *ModerationService) subscribeCommandFetch() error {
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
func (s *ModerationService) consumeCommandFetch(events <-chan *message.Message) {
	for e := range events {
		e.Ack()
		s.registerCommands()
	}
}
