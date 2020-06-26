package com.dyescape.bot.discord.bootstrap;

import com.dyescape.bot.data.repository.ServerRepository;
import com.dyescape.bot.discord.command.ServerPrefixProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerPrefixProviderBootstrap {

    private final ServerRepository repository;

    @Autowired
    public ServerPrefixProviderBootstrap(ServerRepository repository) {
        this.repository = repository;
    }

    @Bean
    public ServerPrefixProvider getProvider() {
        return new ServerPrefixProvider(this.repository);
    }
}
