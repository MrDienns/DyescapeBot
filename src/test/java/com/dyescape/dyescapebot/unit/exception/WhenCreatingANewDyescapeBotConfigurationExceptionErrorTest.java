package com.dyescape.dyescapebot.unit.exception;

import io.vertx.junit5.VertxExtension;

import com.dyescape.dyescapebot.exception.DyescapeBotConfigurationException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
@DisplayName("When creating a new DyescapeBotConfigurationException error")
public class WhenCreatingANewDyescapeBotConfigurationExceptionErrorTest {

    private static DyescapeBotConfigurationException exception;

    @BeforeAll
    public static void setup() {

        exception = new DyescapeBotConfigurationException("hello");
    }

    @Test
    @DisplayName("It should return the specified message")
    public void itShouldReturnReturnTheSpecifiedMessage() {
        assertEquals("hello", exception.getMessage());
    }
}
