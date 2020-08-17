package com.dyescape.bot.discord.command.resolver;

import com.dyescape.bot.discord.command.resolver.processor.SuggestionProcessor;
import com.dyescape.bot.discord.domain.Suggestion;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.JDACommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;

public class SuggestionResolver implements ContextResolver<Suggestion, JDACommandExecutionContext> {

    @Override
    public Suggestion getContext(JDACommandExecutionContext context) throws InvalidCommandArgument {
        String argument = context.popFirstArg();
        return new SuggestionProcessor().process(argument);
    }
}
