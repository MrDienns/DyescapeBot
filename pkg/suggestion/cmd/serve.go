package cmd

import (
	"os"
	"os/signal"
	"syscall"

	"github.com/spf13/viper"

	"github.com/Dyescape/DyescapeBot/pkg/suggestion/service"

	"github.com/Dyescape/DyescapeBot/internal/app/log"

	"github.com/Dyescape/DyescapeBot/internal/discord"

	config "github.com/Dyescape/DyescapeBot/internal/configuration"
	cmdconf "github.com/Dyescape/DyescapeBot/pkg/command/config"

	"github.com/spf13/cobra"
)

var (
	serveCmd = &cobra.Command{
		Use:   "serve",
		Short: "Start the suggestion service",
		Long: `Start the suggestion verification service in which will allow users to use Discord commands to post 
suggestions.`,
		Run: func(cmd *cobra.Command, _ []string) {

			logger := log.NewLogger()
			servConf := cmdconf.KafkaConfig()
			token := viper.GetString("discord.token")

			serv := discord.NewService("Bot " + token)
			if err := serv.Connect(); err != nil {
				logger.Error(err.Error())
				os.Exit(1)
			}

			configReader := config.NewFlatFileConfigReader("data")
			suggestionServ := service.NewSuggestionService(serv, servConf, log.NewWatermillLogger(logger), configReader)
			if err := suggestionServ.Start(); err != nil {
				logger.Error(err.Error())
				os.Exit(1)
			}

			// Wait here until CTRL-C or other term signal is received.
			logger.Info("Bot is now running. Press CTRL-C to exit.")
			sc := make(chan os.Signal, 1)
			signal.Notify(sc, syscall.SIGINT, syscall.SIGTERM, os.Interrupt, os.Kill)
			<-sc

			// Cleanly close down the Discord session.
			serv.Session.Close()
		},
	}
)

func init() {
	rootCmd.AddCommand(serveCmd)
}
