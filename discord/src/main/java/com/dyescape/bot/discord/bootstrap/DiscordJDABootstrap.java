package com.dyescape.bot.discord.bootstrap;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;

@Configuration
public class DiscordJDABootstrap {

    private static final String TOKEN_PREFIX = "Bot ";

    private final String token;

    @Autowired
    public DiscordJDABootstrap(@Value("${discord.token}") String token) {
        this.token = stripOptionalBotPrefix(token);
    }

    @Bean
    public JDA getJDA() throws LoginException {
        JDABuilder builder = JDABuilder.create(this.token,
                GatewayIntent.GUILD_BANS,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_MESSAGES
        );

        builder.disableCache(
                CacheFlag.ACTIVITY,
                CacheFlag.VOICE_STATE,
                CacheFlag.EMOTE,
                CacheFlag.CLIENT_STATUS
        );

        return builder.build();
    }

    String stripOptionalBotPrefix(String token) {
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }
        return token;
    }
}
