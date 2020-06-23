package com.dyescape.bot.discord.bootstrap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When bootstrapping JDA Discord with bot prefixed token")
public class WhenBootstrapingJDADiscordWithBotPrefixedTokenTest {

    @Test
    @DisplayName("It should strip the prefix from the token")
    public void itShouldStripThePrefixFromTheToken() {
        String token = "Bot myCoolPrefixedToken";
        String newToken = new DiscordJDABootstrap(token).stripOptionalBotPrefix(token);
        assertEquals(newToken, "myCoolPrefixedToken", "Prefix was not stripped from token");
    }
}
