package com.dyescape.dyescapebot.configuration.discord;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class containing all Discord specific configuration options.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
@Configuration
@ConfigurationProperties(prefix = "discord")
public class DiscordConfiguration {

    private String token;

    /**
     * Returns the Discord token used to connect to the gateway.
     * @return {@link String}
     * @author Dennis van der Veeke
     * @since 0.1.0
     */
    public String getToken() {
        return this.token;
    }

    /**
     * Setter to set the Discord API token.
     * <b>Note:</b> This is purely here for Spring and should not be called manually!
     * @deprecated
     * @param token {@link String} Discord API token
     * @author Dennis van der Veeke
     * @since 0.1.0
     */
    public void setToken(String token) {
        this.token = token;
    }
}
