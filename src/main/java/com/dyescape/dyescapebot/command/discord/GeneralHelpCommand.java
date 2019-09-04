package com.dyescape.dyescapebot.command.discord;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.HelpCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ACF (Annotation Command Framework) class to register a custom help command.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
@CommandAlias("!")
public class GeneralHelpCommand extends BaseCommand {

    // -------------------------------------------- //
    // COMMANDS
    // -------------------------------------------- //

    @HelpCommand
    public void help(JDACommandEvent e, CommandHelp help) {

        if (e.getIssuer().getAuthor().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        // Stream & filter all relevant commands for the user
        List<HelpEntry> availableCommands = help.getHelpEntries().stream()
                .filter(HelpEntry::shouldShow)
                .filter(entry -> !entry.getCommand().equals(entry.getCommandPrefix() + " " + "help"))
                .collect(Collectors.toList());

        // If it's not empty
        if (!availableCommands.isEmpty()) {

            // Prepare a response
            StringBuilder totalHelpResponse = new StringBuilder();

            // Append the response
            for (HelpEntry entry : availableCommands) {
                totalHelpResponse.append(this.formatHelpEntry(entry));
            }

            // Send the response
            e.sendMessage(this.embed("List of commands", totalHelpResponse.toString()));
        } else {

            // User cannot do anything, send response
            e.sendMessage(this.embed(null,
                    "Cannot visualise help page because you don't have permission to any of my commands."));
        }
    }

    // -------------------------------------------- //
    // PRIVATE
    // -------------------------------------------- //

    private String formatHelpEntry(HelpEntry entry) {

        // We start by simply adding the command string
        StringBuilder builder = new StringBuilder(String.format("`!%s`", entry.getCommand()));

        // If it has a description, we add it
        if (entry.getDescription() != null && !entry.getDescription().isEmpty()) {
            builder.append(" - ").append(entry.getDescription());
        }

        builder.append("\n");

        return builder.toString();
    }

    private MessageEmbed embed(String title, String description) {
        EmbedBuilder eb = new EmbedBuilder();
        if (title != null && !title.isEmpty()) {
            eb.setTitle(title);
        }
        eb.setDescription(description);
        eb.setColor(Color.GRAY);
        return eb.build();
    }

    private MessageEmbed embed(String message, Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(message);
        eb.setColor(color);
        return eb.build();
    }
}
