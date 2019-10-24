package configuration

import (
	"os"
	"testing"

	"github.com/stretchr/testify/assert"
)

type testConfig struct {
	Foo string `json:"foo"`
}

func TestFlatFileConfigReader_ReadConfigurationUncached(t *testing.T) {
	r := NewFlatFileConfigReader("configuration_test")
	tc := testConfig{}
	err := r.GetConfiguration("test", &tc)
	assert.NoError(t, err)
	assert.Equal(t, "bar", tc.Foo)
}

func TestFlatFileConfigReader_ReadConfigurationCached(t *testing.T) {
	r := NewFlatFileConfigReader("configuration_test")
	tc := testConfig{Foo: "foo"}
	r.cache["configuration_test/test.json"] = tc
	err := r.GetConfiguration("test", &tc)
	assert.NoError(t, err)
	assert.Equal(t, "foo", tc.Foo)
}

func TestFlatFileConfigReader_ReadConfigurationNonExistent(t *testing.T) {
	r := NewFlatFileConfigReader("configuration_test")
	tc := testConfig{}
	err := r.ReadConfiguration("i-dont-exist", &tc)
	assert.Error(t, err)
	assert.Equal(t, "", tc.Foo)
}

func TestFlatFileConfigReader_SaveConfiguration(t *testing.T) {
	tc := &testConfig{"bar"}
	r := NewFlatFileConfigReader("configuration_test")
	err := r.SaveConfiguration("temp", tc)
	assert.NoError(t, err)
	read := &testConfig{}
	err = r.GetConfiguration("temp", read)
	assert.NoError(t, err)
	assert.Equal(t, tc, read)
	os.Remove("configuration_test/temp.json")
}
