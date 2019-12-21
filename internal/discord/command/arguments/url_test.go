package arguments

import (
	"net/url"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestUrlResolver_ResolveWithHttpProtocol(t *testing.T) {
	ur := &UrlResolver{}
	val, err := ur.Resolve("http://dyescape.com")
	assert.NoError(t, err)
	assert.Equal(t, getUrl("http://dyescape.com"), val)
}

func TestUrlResolver_ResolveWithHttsProtocol(t *testing.T) {
	ur := &UrlResolver{}
	val, err := ur.Resolve("https://dyescape.com")
	assert.NoError(t, err)
	assert.Equal(t, getUrl("https://dyescape.com"), val)
}

func TestUrlResolver_ResolveWithoutProtocol(t *testing.T) {
	ur := &UrlResolver{}
	val, err := ur.Resolve("dyescape.com")
	assert.NoError(t, err)
	assert.Equal(t, getUrl("https://dyescape.com"), val)
}

func TestUrlResolver_ResolveInvalid(t *testing.T) {
	ur := &UrlResolver{}
	val, err := ur.Resolve("i'm invalid")
	assert.Error(t, err)
	assert.Nil(t, val)
}

func getUrl(u string) *url.URL {
	r, _ := url.Parse(u)
	return r
}
