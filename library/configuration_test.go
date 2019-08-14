package library

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

type testConfig struct {
	Foo string `json:"foo"`
}

func TestLoadExistingConfiguration(t *testing.T) {
	r := NewFlatFileConfigReader("configuration_test")
	tc := testConfig{}
	err := r.ReadConfiguration("test", &tc)
	assert.NoError(t, err)
	assert.Equal(t, tc.Foo, "bar")
}

func TestLoadNonExistingConfiguration(t *testing.T) {
	r := NewFlatFileConfigReader("configuration_test")
	tc := testConfig{}
	err := r.ReadConfiguration("i-dont-exist", &tc)
	assert.Error(t, err)
	assert.Equal(t, tc.Foo, "")
}
