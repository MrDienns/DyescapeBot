package cmd

import "github.com/spf13/cobra"

var (
	rootCmd = &cobra.Command{
		Use:   "command",
		Short: "Used to manage and communicate bot commands",
		Long:  `Allows additional services and modules to register their commands.`,
		Run: func(cmd *cobra.Command, _ []string) {
			cmd.Help()
		},
	}
)

// Execute is the main entrypoint for the root command. This function
// will invoke the Cobra implementation of the command, which, in this
// case, will output the usage guides.
func Execute() {
	rootCmd.Execute()
}
