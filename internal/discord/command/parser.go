package command

import (
	"fmt"
	"strings"
)

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
func (p *Parser) Parse(cmdStr, argsStr string) (map[string]interface{}, error) {
	ret := make(map[string]interface{}, 0)
	if cmd, ok := p.Registry.Commands[cmdStr]; ok {
		spacedArgs := strings.Split(argsStr, " ")
		for i, cmdArg := range cmd.Args {
			if cmdArg.Concat {
				// TODO: Take all remaining spaced args
				continue
			}

			if len(spacedArgs) <= i && !cmdArg.Optional {
				return nil, fmt.Errorf("missing mandatory argument '%v'", cmdArg.Name)
				continue
			}

			spacedArg := sanitizeArg(spacedArgs[i])
			if spacedArg == "" && !cmdArg.Optional {
				return nil, fmt.Errorf("missing mandatory argument '%v'", cmdArg.Name)
			}

			if resolver, ok := p.Registry.Resolvers[cmdArg.Type]; ok {
				parsedArg, err := resolver.Resolve(spacedArg)
				if err != nil {
					return nil, err
				}
				ret[cmdArg.Name] = parsedArg
			}
		}
		return ret, nil
	}
	return nil, fmt.Errorf("unknown command while parsing")
}

func sanitizeArg(arg string) string {
	return strings.TrimSpace(arg)
}
