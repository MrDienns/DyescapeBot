package com.dyescape.bot.discord.command.resolver;

import com.dyescape.bot.data.entity.UserEntity;
import com.dyescape.bot.data.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final JDA jda;
    private final ArgumentProcessor<String> processor;
    private final ArgumentValidator validator;

    public UserResolver(@NotNull UserRepository userRepository, @NotNull JDA jda) {
        this.userRepository = userRepository;
        this.jda = jda;
        this.processor = new UserProcessor();
        this.validator = new UserValidator();
    }

    @Override
    public User getContext(JDACommandExecutionContext context) throws InvalidCommandArgument {

        // Validate and process the argument
        String argument = this.processor.process(context.popFirstArg());
        this.validator.validate(argument);

        // Get our user from Discord
        net.dv8tion.jda.api.entities.User jdaUser = this.jda.getUserById(argument);
        if (jdaUser == null) {
            throw new InvalidCommandArgument("User does not exist.");
        }

        // Get or create our user in our database
        Optional<UserEntity> userEntityResult = this.userRepository.findById(argument);
        UserEntity userEntity;
        if (userEntityResult.isPresent()) {
            userEntity = userEntityResult.get();
        } else {
            userEntity = new UserEntity(jdaUser.getId());
            this.userRepository.save(userEntity);
        }

        // Create a domain model implementation with it
        return new DiscordUser(userEntity, jdaUser);
    }
}
