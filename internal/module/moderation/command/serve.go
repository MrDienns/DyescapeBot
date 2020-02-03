package cmd

import (
	"os"
	"os/signal"
	"syscall"

	"github.com/Dyescape/DyescapeBot/pkg/command/config"

	"github.com/spf13/viper"

	"github.com/Dyescape/DyescapeBot/internal/app/log"
	"github.com/Dyescape/DyescapeBot/pkg/moderation/service"

	"github.com/Dyescape/DyescapeBot/internal/discord"

	"github.com/spf13/cobra"
)

var (
	serveCmd = &cobra.Command{
		Use:   "serve",
		Short: "Start the moderation service",
		Long:  `Start the moderation service so that moderation commands & controls become available in Discord.`,
		Run: func(cmd *cobra.Command, _ []string) {

			logger := log.NewLogger()
			servConf := config.KafkaConfig()
			token := viper.GetString("discord.token")

			serv := discord.NewService("Bot "+token, "Moderation", logger)
			if err := serv.Connect(); err != nil {
				logger.Error(err.Error())
				os.Exit(1)
			}

			moderationServuce := service.NewModerationService(serv, servConf, log.NewWatermillLogger(logger))
			if err := moderationServuce.Start(); err != nil {
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
