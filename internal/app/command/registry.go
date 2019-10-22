package command

import (
	"net/url"
	"reflect"
)

// Registry struct keeps track of the registered argument resolvers and commands.
type Registry struct {
	Resolvers map[reflect.Type]Resolver
	Commands  map[string]*Command
}

// NewRegistry will create a new *Registry struct with the default resolvers.
func NewRegistry() *Registry {
	return &Registry{
		Resolvers: map[reflect.Type]Resolver{
			reflect.TypeOf(0):         IntegerResolver{},
			reflect.TypeOf(""):        StringResolver{},
			reflect.TypeOf(url.URL{}): UrlResolver{},
		},
		Commands: map[string]*Command{},
	}
}
