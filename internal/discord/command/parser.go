package command

import "errors"

// Parser struct is a simple struct that has a *Registry has field.
type Parser struct {
	Registry *Registry
}

// NewParser will take a *Registry pointer and return a new *Parser.
func NewParser(registry *Registry) *Parser {
	return &Parser{registry}
}

// Parse function will take the *Command object and the actual executed command arguments in the form of a string
// slice. It will check if the amount of arguments are the same. Then, it will try to parse every command string
// individually and register the parsed argument in a map which is used as response value. If it failed to parse,
// the parsing is stopped and the error is returned.
func (p *Parser) Parse(cmd *Command, args []string) (map[string]interface{}, error) {
	if len(args) != len(cmd.Args) {
		return nil, errors.New("not enough arguments")
	}
	parsed := make(map[string]interface{}, len(cmd.Args))
	index := 0
	for name, typ := range cmd.Args {
		if resolver, ok := p.Registry.Resolvers[typ]; ok {
			val, err := resolver.Resolve(args[index])
			if err != nil {
				return nil, err
			}
			parsed[name] = val
		}
		index++
	}
	return parsed, nil
}
