package com.dyescape.bot.discord.command.resolver;

import com.dyescape.bot.data.entity.data.UserEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.command.resolver.processor.ArgumentProcessor;
import com.dyescape.bot.discord.command.resolver.processor.UserProcessor;
import com.dyescape.bot.discord.command.resolver.validator.ArgumentValidator;
import com.dyescape.bot.discord.command.resolver.validator.UserValidator;
import com.dyescape.bot.discord.domain.DiscordUser;
import com.dyescape.bot.domain.model.User;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.JDACommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class UserResolver implements ContextResolver<User, JDACommandExecutionContext> {

    private final DataSuit dataSuit;
    private final JDA jda;
    private final ArgumentProcessor<net.dv8tion.jda.api.entities.User> processor;
    private final ArgumentValidator validator;

    public UserResolver(@NotNull DataSuit dataSuit, @NotNull JDA jda) {
        this.dataSuit = dataSuit;
        this.jda = jda;
        this.processor = new UserProcessor(this.jda);
        this.validator = new UserValidator();
    }

    @Override
    public User getContext(JDACommandExecutionContext context) throws InvalidCommandArgument {

        // Validate and process the argument
        String arg = context.popFirstArg();
        this.validator.validate(arg);

        // Get our user from Discord
        net.dv8tion.jda.api.entities.User user = this.processor.process(arg);
        if (user == null) {
            throw new InvalidCommandArgument("User does not exist, or cannot be found. Please use the full numeric " +
                    "user ID (not discriminator!) when loading a user that's not in the server.");
        }

        // Get or create our user in our database
        Optional<UserEntity> userEntityResult = this.dataSuit.getUserRepository().findById(arg);
        UserEntity userEntity;
        if (userEntityResult.isPresent()) {
            userEntity = userEntityResult.get();
        } else {
            userEntity = new UserEntity(user.getId());
            this.dataSuit.getUserRepository().save(userEntity);
        }

        // Create a domain model implementation with it
        return new DiscordUser(this.dataSuit, userEntity, user);
    }
}
