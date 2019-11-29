package service

type KafkaConfig struct {
	Brokers                []string
	CommandCalledTopic     string
	CommandRegisteredTopic string
	CommandFetchTopic      string
}
