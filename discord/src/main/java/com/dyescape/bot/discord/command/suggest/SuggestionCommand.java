package com.dyescape.bot.discord.command.suggest;

import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.command.BotCommand;
import com.dyescape.bot.discord.domain.Suggestion;

import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class SuggestionCommand extends BotCommand {

    public SuggestionCommand(JDA jda, DataSuit dataSuit) {
        super(jda, dataSuit);
    }

    @CommandAlias("suggest|suggestion")
    @Syntax("<Suggestion Thread URL>")
    @Description("Create a suggestion for the suggestions channel, with the use of a suggestion thread")
    public void onTest(JDACommandEvent e, Suggestion suggestion) {

        try {
            Guild guild = e.getIssuer().getGuild();

            // TODO: Make this configurable
            List<TextChannel> channels = guild.getTextChannelsByName("suggestions", false);
            if (channels.isEmpty()) {
                return;
            }

            String message = String.format("Suggested by %s:\n%s", e.getIssuer().getAuthor().getAsMention(), suggestion.getUrl());

            channels.get(0).sendMessage(message).queue(success -> {

                // TODO: Configurable
                success.addReaction(guild.getEmotesByName("agree", true).get(0)).queue();
                success.addReaction(guild.getEmotesByName("neutral", true).get(0)).queue();
                success.addReaction(guild.getEmotesByName("disagree", true).get(0)).queue();
            });
        } catch (Exception ex) {
            this.error(e.getIssuer().getChannel(), ex);
        }
    }
}
