package log

import (
	"go.uber.org/zap"
)

// Logger is a simple struct that acts as an abstract adapter. It exposes a general set of logging functionalities that
// simply invoke the underlying logging framework.
type Logger struct {
	log *zap.Logger
}

// NewLogger returns a new *Logger.
func NewLogger() *Logger {
	logger, _ := zap.NewProduction()
	defer logger.Sync()
	return &Logger{logger}
}

// Info takes a message and logs it on the info level.
func (l Logger) Info(msg string) {
	l.log.Info(msg)
}

// Debug takes a message and logs it on the debug level.
func (l Logger) Debug(msg string) {
	l.log.Debug(msg)
}

// Warn takes a message and logs it on the warn level.
func (l Logger) Warn(msg string) {
	l.log.Warn(msg)
}

// Error takes a message and logs it on the error level.
func (l Logger) Error(msg string) {
	l.log.Error(msg)
}
