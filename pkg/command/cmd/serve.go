package cmd

import (
	"fmt"
	"os"
	"os/signal"
	"syscall"

	"github.com/Dyescape/DyescapeBot/pkg/command/service"

	"github.com/Dyescape/DyescapeBot/internal/app/discord"

	"github.com/spf13/cobra"
)

var (
	serveCmd = &cobra.Command{
		Use:   "serve",
		Short: "Start the command service",
		Long:  `Start the command service so that other modules can register their commands.`,
		Run: func(cmd *cobra.Command, _ []string) {
			token, err := cmd.Flags().GetString("token")
			if err != nil {
				fmt.Println(err.Error())
				os.Exit(1)
			}

			serv := discord.NewService(fmt.Sprintf("Bot %s", token))
			err = serv.Connect()
			if err != nil {
				fmt.Println(err.Error())
				os.Exit(2)
			}

			cmdServ := service.NewCommandService(serv, []string{"localhost:9092"}, "CommandCalledStream",
				"CommandRegisteredStream", "CommandFetchStream")
			err = cmdServ.Start()
			if err != nil {
				fmt.Println(err.Error())
				os.Exit(3)
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
	rootCmd.AddCommand(serveCmd)
}
