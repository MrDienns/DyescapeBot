package com.dyescape.bot.discord.command.resolver.validator;

import co.aikar.commands.InvalidCommandArgument;

import java.util.regex.Pattern;

public class UserValidator implements ArgumentValidator {

    private static final Pattern USER_ID = Pattern.compile("[<@!]*?\\d+[>]?");

    @Override
    public void validate(String argument) throws InvalidCommandArgument {
        if (!USER_ID.matcher(argument).matches()) {
            throw new InvalidCommandArgument("The user ID you specified was not valid. Please tag the user, or specify" +
                    " the full (numeric) ID of the user.");
        }
    }
}
