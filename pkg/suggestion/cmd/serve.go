package cmd

import (
	"fmt"
	"os"
	"os/signal"
	"syscall"

	"github.com/Dyescape/DyescapeBot/pkg/suggestion/service"

	cmdService "github.com/Dyescape/DyescapeBot/pkg/command/service"

	"github.com/Dyescape/DyescapeBot/internal/app/log"

	"github.com/Dyescape/DyescapeBot/internal/app/discord"

	config "github.com/Dyescape/DyescapeBot/internal/app/configuration"

	"github.com/spf13/cobra"
)

var (
	serveCmd = &cobra.Command{
		Use:   "serve",
		Short: "Start the suggestion service",
		Long: `Start the suggestion verification service in which will allow users to use Discord commands to post 
suggestions.`,
		Run: func(cmd *cobra.Command, _ []string) {

			// Build the logger and configuration
			logger := log.NewWatermillLogger(log.NewLogger())
			cmdConf := &cmdService.KafkaConfig{
				Brokers:                []string{"localhost:9092"},
				CommandCalledTopic:     "CommandCalledStream",
				CommandRegisteredTopic: "CommandRegisteredStream",
				CommandFetchTopic:      "CommandFetchStream",
			}

			configReader := config.NewFlatFileConfigReader("data")
			token, err := cmd.Flags().GetString("token")
			if err != nil {
				panic(err)
			}

			serv := service.NewSuggestionService(discord.NewService(fmt.Sprintf("Bot %s", token)),
				cmdConf, logger, configReader)
			err = serv.Connect()
			if err != nil {
				fmt.Println(err.Error())
				os.Exit(1)
			}
			err = serv.Start()
			if err != nil {
				fmt.Println(err.Error())
				os.Exit(2)
			}

			// Wait here until CTRL-C or other term signal is received.
			fmt.Println("Bot is now running. Press CTRL-C to exit.")
			sc := make(chan os.Signal, 1)
			signal.Notify(sc, syscall.SIGINT, syscall.SIGTERM, os.Interrupt, os.Kill)
			<-sc

			// Cleanly close down the Discord session.
			serv.Session.Close()
		},
	}
)

func init() {
	serveCmd.Flags().StringP("token", "t", "", "Discord API token")
	serveCmd.Flags().Bool("api", false, "enable the restful API")
	serveCmd.Flags().Int("port", 8080, "specify the port for the REST API to run on")
	rootCmd.AddCommand(serveCmd)
}
