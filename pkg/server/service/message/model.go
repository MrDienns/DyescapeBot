package message

const (
	Raw = iota
	Embed
)

// Response is an abstract message service response which domain logic consuming messages can return.
type Response struct {
	Title, Message string
	Type           int
}
