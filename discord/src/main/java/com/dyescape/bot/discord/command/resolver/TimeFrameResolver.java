package com.dyescape.bot.discord.command.resolver;

import com.dyescape.bot.discord.command.model.TimeFrame;
import com.dyescape.bot.discord.command.resolver.processor.ArgumentProcessor;
import com.dyescape.bot.discord.command.resolver.processor.TimeFrameProcessor;
import com.dyescape.bot.discord.command.resolver.validator.ArgumentValidator;
import com.dyescape.bot.discord.command.resolver.validator.TimeFrameValidator;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.JDACommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;

public class TimeFrameResolver implements ContextResolver<TimeFrame, JDACommandExecutionContext> {

    private final ArgumentProcessor<TimeFrame> processor;
    private final ArgumentValidator validator;

    public TimeFrameResolver() {
        this.processor = new TimeFrameProcessor();
        this.validator = new TimeFrameValidator();
    }

    @Override
    public TimeFrame getContext(JDACommandExecutionContext context) throws InvalidCommandArgument {
        // Validate and process the argument
        String argumentString = context.popFirstArg();
        this.validator.validate(argumentString);
        return this.processor.process(argumentString);
    }
}
