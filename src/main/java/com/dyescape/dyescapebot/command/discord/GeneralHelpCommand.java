package com.dyescape.dyescapebot.command.discord;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.HelpCommand;
import com.google.common.base.Strings;

@CommandAlias("!")
public class GeneralHelpCommand extends BaseCommand {

    @HelpCommand
    public void help(JDACommandEvent e, CommandHelp help) {

        String header = String.format("**——————[** Showing commands for %s **]——————**\n\n",
                "!" + help.getCommandPrefix());

        StringBuilder totalHelpResponse = new StringBuilder(header);

        for (HelpEntry entry : help.getHelpEntries()) {
            if (!entry.shouldShow()) continue;
            if (entry.getCommand().equals(entry.getCommandPrefix() + " " + "help")) continue;
            totalHelpResponse.append(this.formatHelpEntry(entry));
        }

        e.sendMessageInternal(totalHelpResponse.toString());
    }

    private String formatHelpEntry(HelpEntry entry) {

        // We start by simply adding the command string
        StringBuilder builder = new StringBuilder(entry.getCommand());

        // If it has any arguments, we add those
        if (!Strings.isNullOrEmpty(entry.getParameterSyntax())) {
            builder.append(" ").append(entry.getParameterSyntax());
        }

        // If it has a description, we add it
        if (!Strings.isNullOrEmpty(entry.getDescription())) {
            builder.append(" - ").append(entry.getDescription());
        }

        builder.append("\n");

        return builder.toString();
    }
}
