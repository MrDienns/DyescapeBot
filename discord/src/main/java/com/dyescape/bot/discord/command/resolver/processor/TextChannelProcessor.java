package com.dyescape.bot.discord.command.resolver.processor;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextChannelProcessor implements ArgumentProcessor<TextChannel> {

    private static final Pattern CHANNEL_ID = Pattern.compile("[<#!]*?\\d+[>]?");
    private final JDA jda;

    public TextChannelProcessor(JDA jda) {
        this.jda = jda;
    }

    @Override
    public TextChannel process(String argument) {

        Matcher matcher = CHANNEL_ID.matcher(argument);

        if (matcher.matches()) {
            argument = argument
                    .replace("<", "")
                    .replace("#", "")
                    .replace("!", "")
                    .replace(">", "");

            return this.jda.getTextChannelById(argument);
        }

        return null;
    }
}
