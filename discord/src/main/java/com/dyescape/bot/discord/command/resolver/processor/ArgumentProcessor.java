package com.dyescape.bot.discord.command.resolver.processor;

public interface ArgumentProcessor<Result> {

    Result process(String argument);
}
