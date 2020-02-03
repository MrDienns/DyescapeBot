package cmd

import (
	"github.com/Dyescape/DyescapeBot/internal/cobracmd"

	"github.com/spf13/cobra"
)

var (
	rootCmd = &cobra.Command{
		Use:   "moderation",
		Short: "Used to control the moderation module",
		Long:  `Allows for complex moderation to be done on the Discord server`,
		Run: func(cmd *cobra.Command, _ []string) {
			cmd.Help()
		},
	}
)

// Execute is the main entrypoint for the root command. This function will invoke the Cobra implementation of the
// command, which, in this case, will output the usage guides.
func Execute() {
	rootCmd.Execute()
}

// init gets called by Cobra and serves as command initialisation entry point.
func init() {
	cobracmd.NewBootstrap("moderation", rootCmd).Bootstrap()
}
