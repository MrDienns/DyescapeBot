package cobracmd

import (
	"fmt"
	"os"

	"github.com/Dyescape/DyescapeBot/internal/app/log"

	"github.com/mitchellh/go-homedir"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

type bootstrap struct {
	ConfigFile string
	Module     string
	Command    *cobra.Command
}

// NewBootstrap accepts the required parameters and passes them into a new *bootstrap, which is then returned.
func NewBootstrap(module string, command *cobra.Command) *bootstrap {
	return &bootstrap{Module: module, Command: command}
}

// Bootstrap bootstraps the command, the configuration and the environment values.
func (b *bootstrap) Bootstrap() {
	cobra.OnInitialize(b.initConfig)
	b.Command.PersistentFlags().StringVar(&b.ConfigFile, "config", "", "config file")
}

// initConfig will read the configuration and load it into memory when a config file was found.
func (b *bootstrap) initConfig() {
	logger := log.NewLogger()
	if b.ConfigFile != "" {
		viper.SetConfigFile(b.ConfigFile)
	} else {
		viper.AddConfigPath(".")
		viper.AddConfigPath(b.homeDir(logger))
		viper.AddConfigPath("/opt/dyescape/bot/" + b.Module)
		viper.SetConfigName(b.Module)
	}
	viper.AutomaticEnv()
	err := viper.ReadInConfig()
	if err != nil {
		logger.Error(err.Error())
		os.Exit(1)
	}
	logger.Info(fmt.Sprintf("Using config: %v", viper.ConfigFileUsed()))
}

// homeDir is a simple function that gets and retrieves the home directory for the current user.
func (b *bootstrap) homeDir(log *log.Logger) string {
	home, err := homedir.Dir()
	if err != nil {
		log.Warn(err.Error())
	}
	return home
}
