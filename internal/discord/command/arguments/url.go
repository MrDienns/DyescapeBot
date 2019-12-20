package arguments

import (
	"fmt"
	"net/url"
	"strings"
)

// UrlResolver will try to parse the provided argument to a URL.
type UrlResolver struct{}

func (ur UrlResolver) Resolve(arg string) (interface{}, error) {
	if !(strings.HasPrefix(arg, "http://") || strings.HasPrefix(arg, "https://")) {
		arg = "https://" + arg
	}
	u, err := url.Parse(arg)
	if err != nil {
		return nil, fmt.Errorf("invalid, please specify a URL")
	}
	return u, nil
}
