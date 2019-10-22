package library

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
)

// ConfigReader is a generic implementation used to define a configuration reader. It can
// be used to read the configuration from the specific implementation, get the configuration
// from the cache or save the configuration.
type ConfigReader interface {
	ReadConfiguration(id string, configObj interface{}) error
	GetConfiguration(id string, configObj interface{}) error
	SaveConfiguration(id string, configObj interface{}) error
}

// FlatFileConfigReader is a flatfile based implementation of the ConfigReader interface which
// will load configuration files directly from the disk in JSON format. The folderPath property
// is used to indicate in what folder the configurations are located.
type FlatFileConfigReader struct {
	folderPath string
	cache      map[string]interface{}
}

// NewFlatFileConfigReader constructs a new FlatFileConfigReader struct with the passed folder path
// for the file configurations.
func NewFlatFileConfigReader(folderPath string) *FlatFileConfigReader {
	return &FlatFileConfigReader{folderPath: folderPath, cache: make(map[string]interface{}, 0)}
}

// ReadConfiguration will read and return the provided unmarshalled struct that belongs to
// the provided id. When the configuration was read, it is cached in a map. This function
// will always read from the disk; see GetConfiguration instead for caching.
func (r FlatFileConfigReader) ReadConfiguration(id string, configObj interface{}) error {
	path := r.getFilePath(id)
	b, err := ioutil.ReadFile(path)
	if err != nil {
		return err
	}
	err = json.Unmarshal(b, configObj)
	if err != nil {
		return err
	}
	r.cache[path] = configObj
	return nil
}

// GetConfiguration will try to get the configuration from a cache, if it exists. If it exists,
// it is returned from the cache. If it does not exist, it will load the configuration from the
// disk and cache it.
func (r FlatFileConfigReader) GetConfiguration(id string, configObj interface{}) error {
	path := r.getFilePath(id)
	if cnf, ok := r.cache[path]; ok {
		configObj = cnf
		return nil
	}
	return r.ReadConfiguration(id, configObj)
}

// SaveConfiguration will save the configuration back to the disk using the ID that was provided.
// Configuration is saved with 0664 permission node.
func (r FlatFileConfigReader) SaveConfiguration(id string, configObj interface{}) error {
	path := r.getFilePath(id)
	b, err := json.Marshal(configObj)
	if err != nil {
		return err
	}
	err = ioutil.WriteFile(path, b, 0664)
	if err != nil {
		return err
	}
	r.cache[id] = configObj
	return nil
}

// getFilePath will take the specified config directory in the ConfigReader and combine it with
// the ID passed into this function, which will result in the file path for the configuration.
func (r *FlatFileConfigReader) getFilePath(id string) string {
	return fmt.Sprintf("%s/%s.json", r.folderPath, id)
}
