package com.dyescape.dyescapebot.command.discord.resolver;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.JDACommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import com.dyescape.dyescapebot.model.Suggestion;

import java.net.URI;
import java.net.URISyntaxException;

public class SuggestionResolver implements ContextResolver<Suggestion, JDACommandExecutionContext> {

    @Override
    public Suggestion getContext(JDACommandExecutionContext context) throws InvalidCommandArgument {

        // popFirstArgument will always give us the correct argument string
        String argument = context.popFirstArg();

        try {
            URI uri;

            try {
                uri = new URI(argument);
            } catch (URISyntaxException e) {
                throw new InvalidCommandArgument(e.getMessage());
            }

            if (!(uri.getHost().equalsIgnoreCase("www.dyescape.com") ||
                    uri.getHost().equalsIgnoreCase("dyescape.com"))) {

                throw new InvalidCommandArgument("Specified URL must be a dyescape.com link.");
            }

            return new Suggestion(uri.toString());
        } catch (Exception ex) {
            throw new InvalidCommandArgument("Please specify a dyescape.com forum thread");
        }
    }
}
