package com.dyescape.bot.discord.command.resolver.processor;

public class UserProcessor implements ArgumentProcessor {

    @Override
    public String process(String argument) {
        return argument
                .replace("<", "")
                .replace("@", "")
                .replace("!", "")
                .replace(">", "");
    }
}
