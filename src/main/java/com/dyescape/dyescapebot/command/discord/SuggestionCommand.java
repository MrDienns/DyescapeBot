package com.dyescape.dyescapebot.command.discord;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dyescape.dyescapebot.model.Suggestion;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.List;

/**
 * Class which contains the commands used to create suggestion related commands in the Discord server.
 * @author Dennis van der Veeke
 */
@CommandAlias("!")
public class SuggestionCommand extends BaseCommand {

    @Subcommand("suggest|suggestion")
    @Syntax("<Suggestion Thread URL>")
    @Description("Create a suggestion for the suggestions channel, with the use of a suggestion thread")
    public void onChannelMuteCommand(JDACommandEvent e, Suggestion suggestion) {

        if (e.getIssuer().getAuthor().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        Guild guild = e.getIssuer().getGuild();

        // TODO: Make this configurable
        List<TextChannel> channels = guild.getTextChannelsByName("suggestions", false);
        if (channels == null || channels.isEmpty()) {
            return;
        }

        String message = String.format("Suggested by %s:\n%s", e.getIssuer().getAuthor().getAsMention(), suggestion.getUrl());

        channels.get(0).sendMessage(message).queue(success -> {

            success.addReaction(guild.getEmotesByName("disagree", false).get(0)).queue();
            success.addReaction(guild.getEmotesByName("neutral", false).get(0)).queue();
            success.addReaction(guild.getEmotesByName("agree", false).get(0)).queue();
        });
    }

    private MessageEmbed embed(String message, Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(message);
        eb.setColor(color);
        return eb.build();
    }
}
