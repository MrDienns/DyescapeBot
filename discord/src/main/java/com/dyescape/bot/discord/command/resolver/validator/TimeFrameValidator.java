package com.dyescape.bot.discord.command.resolver.validator;

import co.aikar.commands.InvalidCommandArgument;

import java.util.regex.Pattern;

public class TimeFrameValidator implements ArgumentValidator {

    private static final Pattern TIMEFRAME = Pattern.compile("[0-9dhm]+");

    @Override
    public void validate(String argument) throws InvalidCommandArgument {
        if (!TIMEFRAME.matcher(argument).matches()) {
            throw new InvalidCommandArgument("Invalid time frame specified. Please use the following format: 30d6h30m");
        }
    }
}
