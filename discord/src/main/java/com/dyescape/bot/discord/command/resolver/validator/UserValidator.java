package com.dyescape.bot.discord.command.resolver.validator;

import co.aikar.commands.InvalidCommandArgument;

import java.util.regex.Pattern;

public class UserValidator implements ArgumentValidator {

    private static final Pattern USER_ID = Pattern.compile("[<@!]*?\\d+[>]?");
    private static final Pattern USER_DISCRIMINATOR = Pattern.compile("[\\w]+#\\d{4}");

    @Override
    public void validate(String argument) throws InvalidCommandArgument {
        if (!USER_ID.matcher(argument).matches() && !USER_DISCRIMINATOR.matcher(argument).matches()) {
            throw new InvalidCommandArgument("The user ID you specified was not valid. Please tag the user, specify" +
                    "their name and discriminator or specify the full (numeric) ID of the user.");
        }
    }
}
