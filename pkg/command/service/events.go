package service

// CommandCalledEvent is an event fired whenever a user has called a command. Calling, meaning the user has sent the
// command formatted message in Discord and the command module has detected this command request. The event indicates
// that the user is trying to execute this command. It is up to other modules to respond to this command call and
// perform the command.
type CommandCalledEvent struct {
	// User string represents the Discord user ID that called the command.
	User string `json:"user"`
	// Channel is a struct field which represents the channel ID in which the command was called.
	Channel string `json:"channel"`
	// Server is a struct field which represents the guild ID in which the command was called.
	Guild string `json:"guild"`
	// Command string is a prefix trimmed representation of the command message that the user has sent. For instance,
	// if the command is `!! ban Jay`, then this string will contain only `ban Jay`.
	Command string `json:"command"`
}

// CommandRegisteredEvent is an event fired by additional modules to indicate that a command should be registered.
// They will be consumed by the command module in order to communicate the commands that exist over the entire
// application even though the command module isn't aware of any of them directly.
type CommandRegisteredEvent struct {
	// Module represents the name of the module that added the command.
	Module string `json:"module"`
	// Command is the name of the command. For example, when we want to add a `!! ban Jay` command, then `ban` is our
	// value in this event.
	Command string `json:"command"`
	// Arguments is a string which represents a readable format for the arguments of this command.
	Arguments string `json:"arguments"`
}
