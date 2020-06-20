package server

import (
	"os"
	"os/signal"
	"syscall"
)

// Server is an interface used to define abstract bot servers that work on different platforms.
type Server interface {
	Start() error
	RegisterService(Service)
}

// Service is an abstract interface which defines an abstract initialisation of a functionality on a server. The
// instance, the consuming of sent messages; users joining or leaving the platform, etc.
type Service interface {
	Server
	Initialise() error
}

// AwaitShutdown will sleep the process until a cancel signal is provided.
func AwaitShutdown() {
	sc := make(chan os.Signal, 1)
	signal.Notify(sc, syscall.SIGINT, syscall.SIGTERM, os.Interrupt, os.Kill)
	<-sc
}
