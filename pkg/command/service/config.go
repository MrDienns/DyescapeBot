package service

type KafkaConfig struct {
	Brokers              []string
	CommandFetchTopic    string
	CommandCallTopic     string
	CommandRegisterTopic string
}
