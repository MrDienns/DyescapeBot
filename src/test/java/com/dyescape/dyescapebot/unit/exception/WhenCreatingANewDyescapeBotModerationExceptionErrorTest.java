package com.dyescape.dyescapebot.unit.exception;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.junit5.VertxExtension;

import com.dyescape.dyescapebot.exception.DyescapeBotModerationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
@DisplayName("When creating a new DyescapeBotConfigurationException error")
public class WhenCreatingANewDyescapeBotModerationExceptionErrorTest {

    private static DyescapeBotModerationException exception;

    @BeforeAll
    public static void setup() {

        exception = new DyescapeBotModerationException("hello");
    }

    @Test
    @DisplayName("It should return the specified message")
    public void itShouldReturnReturnTheSpecifiedMessage() {
        assertEquals("hello", exception.getMessage());
    }
}
