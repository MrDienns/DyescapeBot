package com.dyescape.bot.discord.bootstrap;

import co.aikar.commands.JDACommandContexts;
import co.aikar.commands.JDACommandManager;
import com.dyescape.bot.data.repository.ServerRepository;
import com.dyescape.bot.discord.command.ServerConfigProvider;
import com.dyescape.bot.discord.command.ServerPrefixProvider;
import com.dyescape.bot.discord.command.configuration.ConfigurationCommand;
import com.dyescape.bot.discord.command.resolver.UserResolver;
import com.dyescape.bot.discord.command.suggest.SuggestionCommand;
import com.dyescape.bot.domain.model.User;
import net.dv8tion.jda.api.JDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ACFBootstrap {

    private final JDA jda;
    private final ServerRepository serverRepository;
    private final ServerPrefixProvider prefixProvider;

    @Autowired
    public ACFBootstrap(JDA jda, ServerRepository serverRepository, ServerPrefixProvider prefixProvider) {
        this.jda = jda;
        this.serverRepository = serverRepository;
        this.prefixProvider = prefixProvider;
    }

    @Bean
    public JDACommandManager getCommandManager() {
        JDACommandManager manager = new JDACommandManager(this.jda);
        manager.setConfigProvider(new ServerConfigProvider(this.prefixProvider));

        // TODO: Permission resolver

        // Get the context
        JDACommandContexts contexts = (JDACommandContexts) manager.getCommandContexts();

        // Register resolvers
        contexts.registerContext(User.class, new UserResolver(this.jda));

        // Register commands
        manager.registerCommand(new SuggestionCommand());
        manager.registerCommand(new ConfigurationCommand(this.serverRepository));
        manager.enableUnstableAPI("help");
        return manager;
    }
}
