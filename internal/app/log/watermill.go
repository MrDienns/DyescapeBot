package log

import "github.com/ThreeDotsLabs/watermill"

// WatermillLogger is an extension of *Logger that implements the WatermillLogger adapter functions.
type WatermillLogger struct {
	*Logger
}

// NewWatermillLogger constructs a new *WatermillLogger.
func NewWatermillLogger(logger *Logger) *WatermillLogger {
	return &WatermillLogger{logger}
}

// Info implements the LoggerAdapter Info function. Invokes the Info function on the parent logger.
func (l WatermillLogger) Info(msg string, fields watermill.LogFields) {
	l.Logger.Info(msg)
}

// Debug implements the LoggerAdapter Debug function. Invokes the Debug function on the parent logger.
func (l WatermillLogger) Debug(msg string, fields watermill.LogFields) {
	l.Logger.Debug(msg)
}

// Error implements the LoggerAdapter Error function. Invokes the Error function on the parent logger.
func (l WatermillLogger) Error(msg string, err error, fields watermill.LogFields) {
	l.Logger.Error(msg)
}

// Trace implements the LoggerAdapter Info function. Invokes the Info function on the parent logger.
func (l WatermillLogger) Trace(msg string, fields watermill.LogFields) {
	l.Logger.Info(msg)
}

// Error implements the LoggerAdapter Error function. Invokes the Error function on the parent logger.
func (l WatermillLogger) With(fields watermill.LogFields) watermill.LoggerAdapter {
	return l
}
