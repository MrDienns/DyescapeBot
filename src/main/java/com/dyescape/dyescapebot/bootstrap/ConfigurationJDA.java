package com.dyescape.dyescapebot.bootstrap;

import com.dyescape.dyescapebot.configuration.discord.DiscordConfiguration;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;

/**
 * Configuration (Bean) class used to bootstrap the JDA (Java Discord API) beans.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
@Configuration
public class ConfigurationJDA {

    private final JDA jda;

    /**
     * Construct a new instance of the {@link ConfigurationJDA} class. Accepts a Discord API token as parameter.
     * @param configuration {@link DiscordConfiguration} object
     * @author Dennis van der Veeke
     * @since 0.1.0
     */
    public ConfigurationJDA(DiscordConfiguration configuration) throws LoginException {
        this.jda = new JDABuilder(configuration.getToken()).build();
    }

    /**
     * Get an instance of the created {@link JDA} class.
     * @return {@link JDA} Created JDA instance
     * @author Dennis van der Veeke
     * @since 0.1.0
     */
    @Bean
    public JDA getJDA() {
        return this.jda;
    }
}
