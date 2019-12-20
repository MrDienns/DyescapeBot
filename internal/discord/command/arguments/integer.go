package arguments

import (
	"errors"
	"fmt"
	"strconv"
)

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
