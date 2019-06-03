package com.dyescape.dyescapebot.unit.configuration.discord;

import com.dyescape.dyescapebot.configuration.discord.DiscordConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When setting values on the DiscordConfiguration object")
public class WhenSettingValuesOnTheDiscordConfigurationObjectTest {

    private static DiscordConfiguration configuration;

    @SuppressWarnings("deprecation")
    @BeforeAll
    public static void setup() {
        configuration = new DiscordConfiguration();
        configuration.setToken("test");
    }

    @Test
    @DisplayName("The Getter should return the set value")
    public void theGetterShouldReturnTheSetValue() {
        assertEquals("test", this.configuration.getToken(), "Token was not set correctly.");
    }
}
