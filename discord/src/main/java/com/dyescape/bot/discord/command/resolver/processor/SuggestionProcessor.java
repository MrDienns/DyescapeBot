package com.dyescape.bot.discord.command.resolver.processor;

import com.dyescape.bot.discord.domain.Suggestion;

import co.aikar.commands.InvalidCommandArgument;

import java.net.URI;
import java.net.URISyntaxException;

public class SuggestionProcessor implements ArgumentProcessor<Suggestion> {

    @Override
    public Suggestion process(String argument) {
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
