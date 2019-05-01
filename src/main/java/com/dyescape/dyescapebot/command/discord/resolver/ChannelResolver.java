package com.dyescape.dyescapebot.command.discord.resolver;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.JDACommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChannelResolver implements ContextResolver<TextChannel, JDACommandExecutionContext> {

    private static final Pattern CHANNEL_PATTERN = Pattern.compile("<#.*?(\\d+)>");

    @Override
    public TextChannel getContext(JDACommandExecutionContext context) throws InvalidCommandArgument {

        // popFirstArgument will always give us the correct argument string
        String argument = context.popFirstArg();

        // Let's get the Discord ID from the tag string
        Matcher idMatcher = CHANNEL_PATTERN.matcher(argument);
        if (!idMatcher.find()) {
            throw new InvalidCommandArgument("Please tag the text channel.");
        }

        String channelId = idMatcher.group(1);
        TextChannel channel = context.getIssuer().getEvent().getGuild().getTextChannelById(channelId);

        if (channel == null) {

            // Nothing found
            throw new InvalidCommandArgument("Channel not found.");
        }

        return channel;
    }
}
