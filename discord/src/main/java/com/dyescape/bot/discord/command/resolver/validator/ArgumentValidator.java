package com.dyescape.bot.discord.command.resolver.validator;

import co.aikar.commands.InvalidCommandArgument;

public interface ArgumentValidator {

    void validate(String argument) throws InvalidCommandArgument;
}
