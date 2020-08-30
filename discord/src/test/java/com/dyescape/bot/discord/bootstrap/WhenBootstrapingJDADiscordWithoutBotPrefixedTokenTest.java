package com.dyescape.bot.discord.bootstrap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When bootstrapping JDA Discord without bot prefixed token")
public class WhenBootstrapingJDADiscordWithoutBotPrefixedTokenTest {

    @Test
    @DisplayName("It should not strip the token")
    public void itShouldNotStripTheToken() {
        String token = "myCoolPrefixedToken";
        String newToken = new DiscordJDABootstrap(token, null).stripOptionalBotPrefix(token);
        assertEquals(newToken, "myCoolPrefixedToken", "The token was unnecessarily stripped");
    }
}
