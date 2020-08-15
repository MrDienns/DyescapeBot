package com.dyescape.bot.discord.bootstrap;

import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.command.ServerConfigProvider;
import com.dyescape.bot.discord.command.ServerPrefixProvider;
import com.dyescape.bot.discord.command.configuration.ConfigurationCommand;
import com.dyescape.bot.discord.command.moderation.ModerationCommand;
import com.dyescape.bot.discord.command.resolver.TimeFrameResolver;
import com.dyescape.bot.discord.command.resolver.UserResolver;
import com.dyescape.bot.domain.model.TimeFrame;
import com.dyescape.bot.domain.model.User;

import co.aikar.commands.JDACommandContexts;
import co.aikar.commands.JDACommandManager;
import net.dv8tion.jda.api.JDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ACFBootstrap {

    private final JDA jda;
    private final DataSuit dataSuit;
    private final ServerPrefixProvider prefixProvider;

    @Autowired
    public ACFBootstrap(JDA jda, DataSuit dataSuit, ServerPrefixProvider prefixProvider) {
        this.jda = jda;
        this.dataSuit = dataSuit;
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
        contexts.registerContext(User.class, new UserResolver(this.dataSuit, this.jda));
        contexts.registerContext(TimeFrame.class, new TimeFrameResolver());

        // Register commands
        manager.registerCommand(new ModerationCommand(this.dataSuit));
        manager.registerCommand(new ConfigurationCommand(this.dataSuit));
        manager.enableUnstableAPI("help");
        return manager;
    }
}
