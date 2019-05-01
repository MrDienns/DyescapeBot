package com.dyescape.dyescapebot.command.discord;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.HelpCommand;
import com.google.common.base.Strings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;

@CommandAlias("!")
public class GeneralHelpCommand extends BaseCommand {

    // -------------------------------------------- //
    // COMMANDS
    // -------------------------------------------- //

    @HelpCommand
    public void help(JDACommandEvent e, CommandHelp help) {

        StringBuilder totalHelpResponse = new StringBuilder();

        for (HelpEntry entry : help.getHelpEntries()) {
            if (!entry.shouldShow()) continue;
            if (entry.getCommand().equals(entry.getCommandPrefix() + " " + "help")) continue;
            totalHelpResponse.append(this.formatHelpEntry(entry));
        }

        e.sendMessage(this.embed("List of commands", totalHelpResponse.toString()));
    }

    // -------------------------------------------- //
    // PRIVATE
    // -------------------------------------------- //

    private String formatHelpEntry(HelpEntry entry) {

        // We start by simply adding the command string
        StringBuilder builder = new StringBuilder(String.format("`!%s", entry.getCommand()));

        // If it has any arguments, we add those
        if (!Strings.isNullOrEmpty(entry.getParameterSyntax())) {
            builder.append(" ").append(entry.getParameterSyntax());
        }
        builder.append("`");

        // If it has a description, we add it
        if (!Strings.isNullOrEmpty(entry.getDescription())) {
            builder.append("\n").append(entry.getDescription());
        }

        builder.append("\n\n");

        return builder.toString();
    }

    private MessageEmbed embed(String title, String description) {
        EmbedBuilder eb = new EmbedBuilder();
        if (!Strings.isNullOrEmpty(title)) {
            eb.setTitle(title);
        }
        eb.setDescription(description);
        eb.setColor(Color.GRAY);
        return eb.build();
    }
}
