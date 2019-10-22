package command

import (
	"errors"
	"fmt"
	"net/url"
	"strconv"
	"strings"
)

// Resolver interface represents a command argument resolver.
type Resolver interface {
	// Resolve accepts a string as argument and returns the object it was tasked to resolve.
	// If it failed, an error is returned instead.
	Resolve(arg string) (interface{}, error)
}

// IntegerResolver is a resolver that will try to transform the argument into an integer.
type IntegerResolver struct{}

// Resolve will try to parse the string as integer. If it failed, an error is returned.
func (ir IntegerResolver) Resolve(arg string) (interface{}, error) {
	i, err := strconv.Atoi(arg)
	if err != nil {
		return nil, errors.New(fmt.Sprintf("could not parse '%s' as integer", arg))
	}
	return i, nil
}

// StringResolver is a default implementation for the resolvers, which basically just returns the original
// passed argument.
type StringResolver struct{}

// Resolve will simply return the original passed argument, considering there is nothing to resolve for this
// implementation.
func (ir StringResolver) Resolve(arg string) (interface{}, error) {
	return arg, nil
}

// UrlResolver will try to parse the provided argument to a URL.
type UrlResolver struct{}

func (ur UrlResolver) Resolve(arg string) (interface{}, error) {
	if !(strings.HasPrefix(arg, "http://") && strings.HasPrefix(arg, "https://")) {
		arg = "https://" + arg
	}
	u, err := url.Parse(arg)
	if err != nil {
		return nil, fmt.Errorf("invalid, please specify a URL")
	}
	return u, nil
}
