package com.dyescape.bot.discord.command.resolver.validator;

import co.aikar.commands.InvalidCommandArgument;
import net.dv8tion.jda.api.JDA;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextChannelValidator implements ArgumentValidator {

    private static final Pattern CHANNEL_ID = Pattern.compile("[<#]*?\\d+[>]?");
    private final JDA jda;

    public TextChannelValidator(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void validate(String argument) throws InvalidCommandArgument {
        Matcher matcher = CHANNEL_ID.matcher(argument);
        if (!matcher.matches()) {
            throw new InvalidCommandArgument("Channel does not exist.");
        }
    }
}
