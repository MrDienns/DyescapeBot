package events

// CommandFetchEvent is an event fired by general command helpers in order to indirectly tell underlying modules that
// they should publish their command register events so that the command handler can consume them and build a command
// registry.
type CommandFetchEvent struct{}
