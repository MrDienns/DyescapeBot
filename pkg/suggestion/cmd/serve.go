package cmd

import (
	"fmt"
	"os"
	"os/signal"
	"syscall"

	lib "github.com/Dyescape/DyescapeBot/internal/app/configuration"

	"github.com/Dyescape/DyescapeBot/pkg/suggestion/service"
	"github.com/spf13/cobra"
)

var (
	api      bool
	serveCmd = &cobra.Command{
		Use:   "serve",
		Short: "Start the suggestion service",
		Long: `Start the suggestion verification service in which
will allow users to use Discord commands to post suggestions.`,
		Run: func(cmd *cobra.Command, _ []string) {
			configReader := lib.NewFlatFileConfigReader("data")
			token, err := cmd.Flags().GetString("token")
			if err != nil {
				panic(err)
			}

			serv := service.NewService(fmt.Sprintf("Bot %s", token), configReader)
			serv.Connect()

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
