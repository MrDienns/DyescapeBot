package service

// command is a high level struct used in the registry (not to be confused with the internal, re-usable command library)
// which is used to keep track of the high level command information. It has the name, the command signature and the
// module which added the command.
type command struct {
	Module    string
	Name      string
	Arguments string
}

// registry is a command module registry (not to be confused with the internal, re-usable command library) which is
// used to keep track of high-level command registrations. It has the name, the command signature and the module
// which added the command.
type registry struct {
	Commands map[string]*command
}
