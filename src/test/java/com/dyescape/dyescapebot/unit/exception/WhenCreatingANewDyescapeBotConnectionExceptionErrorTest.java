package com.dyescape.dyescapebot.unit.exception;

import io.vertx.junit5.VertxExtension;

import com.dyescape.dyescapebot.exception.DyescapeBotConfigurationException;
import com.dyescape.dyescapebot.exception.DyescapeBotConnectionException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
@DisplayName("When creating a new DyescapeBotConnectionException error")
public class WhenCreatingANewDyescapeBotConnectionExceptionErrorTest {

    private static DyescapeBotConnectionException exception;
    private static DyescapeBotConfigurationException cause;

    @BeforeAll
    public static void setup() {

        cause = new DyescapeBotConfigurationException("hello");
        exception = new DyescapeBotConnectionException(cause);
    }

    @Test
    @DisplayName("It should return the specified message")
    public void itShouldReturnReturnTheSpecifiedMessage() {
        assertEquals(String.format("%s: %s", cause.getClass().getCanonicalName(), "hello"), exception.getMessage());
    }

    @Test
    @DisplayName("It should return the specified cause")
    public void itShouldReturnTheSpecifiedCause() {
        DyescapeBotConfigurationException expected = new DyescapeBotConfigurationException("hello");
        assertEquals(expected.getMessage(), exception.getCause().getMessage());
        assertEquals(expected.getClass(), exception.getCause().getClass());
    }
}
