package service

type KafkaConfig struct {
	Brokers              []string
	BootstrapTopic       string
	CommandCallTopic     string
	CommandRegisterTopic string
}
