package config

import (
	"github.com/Dyescape/DyescapeBot/pkg/command/service"
	"github.com/spf13/viper"
)

func KafkaConfig() *service.KafkaConfig {
	return &service.KafkaConfig{
		Brokers:              viper.GetStringSlice("kafka.brokers"),
		CommandFetchTopic:    viper.GetString("kafka.topics.commandFetch"),
		CommandCallTopic:     viper.GetString("kafka.topics.commandCall"),
		CommandRegisterTopic: viper.GetString("kafka.topics.commandRegister"),
	}
}
