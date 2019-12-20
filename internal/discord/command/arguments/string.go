package arguments

// StringResolver is a default implementation for the resolvers, which basically just returns the original
// passed argument.
type StringResolver struct{}

// Resolve will simply return the original passed argument, considering there is nothing to resolve for this
// implementation.
func (ir StringResolver) Resolve(arg string) (interface{}, error) {
	return arg, nil
}
