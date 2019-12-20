package command

// Resolver interface represents a command argument resolver.
type Resolver interface {
	// Resolve accepts a string as argument and returns the object it was tasked to resolve.
	// If it failed, an error is returned instead.
	Resolve(arg string) (interface{}, error)
}
