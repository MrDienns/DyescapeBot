package com.dyescape.dyescapebot.bootstrap;

import com.dyescape.dyescapebot.moderation.Moderation;
import com.dyescape.dyescapebot.moderation.discord.DiscordModeration;
import net.dv8tion.jda.core.JDA;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationModeration {

    private final JDA jda;

    public ConfigurationModeration(JDA jda) {
        this.jda = jda;
    }

    @Bean
    public Moderation moderation() {
        return new DiscordModeration(this.jda);
    }
}
