package configuration

import "testing"

func TestNonExistentConfiguration(t *testing.T) {
	r := NewConfigReader("configuration_test")
	conf := r.ReadConfiguration("i-dont-exist")
	if conf.IsValid() {
		t.Error("Non existing Guild configuration should be marked as invalid")
	}
}

func TestIncompleteConfiguration(t *testing.T) {
	r := NewConfigReader("configuration_test")
	conf := r.ReadConfiguration("incomplete_configured")
	if conf.IsValid() {
		t.Error("Incomplete Guild configuration should be marked as invalid")
	}
}

func TestValidConfiguration(t *testing.T) {
	r := NewConfigReader("configuration_test")
	conf := r.ReadConfiguration("configured")
	if !conf.IsValid() {
		t.Error("Correct Guild configuration should be marked as valid")
	}
}
