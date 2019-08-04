package cmd

import "github.com/spf13/cobra"

var (
	rootCmd = &cobra.Command{
		Use:   "suggestion",
		Short: "Used to manage community suggestions",
		Long: `Manage community suggestions posted through Discord
where users can vote on them.`,
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
