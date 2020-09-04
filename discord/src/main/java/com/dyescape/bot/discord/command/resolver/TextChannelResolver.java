package com.dyescape.bot.discord.command.resolver;

import com.dyescape.bot.discord.command.resolver.processor.ArgumentProcessor;
import com.dyescape.bot.discord.command.resolver.processor.TextChannelProcessor;
import com.dyescape.bot.discord.command.resolver.validator.ArgumentValidator;
import com.dyescape.bot.discord.command.resolver.validator.TextChannelValidator;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.JDACommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

public class TextChannelResolver implements ContextResolver<TextChannel, JDACommandExecutionContext> {

    private final ArgumentProcessor<TextChannel> processor;
    private final ArgumentValidator validator;

    public TextChannelResolver(JDA jda) {
        this.processor = new TextChannelProcessor(jda);
        this.validator = new TextChannelValidator(jda);
    }

    @Override
    public TextChannel getContext(JDACommandExecutionContext context) throws InvalidCommandArgument {
        // Validate and process the argument
        String argumentString = context.popFirstArg();
        this.validator.validate(argumentString);
        return this.processor.process(argumentString);
    }
}
