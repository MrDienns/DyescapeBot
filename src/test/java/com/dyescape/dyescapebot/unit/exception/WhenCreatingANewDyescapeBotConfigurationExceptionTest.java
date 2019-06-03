package com.dyescape.dyescapebot.unit.exception;

import com.dyescape.dyescapebot.exception.DyescapeBotConfigurationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When creating a new DyescapeBotConfigurationException")
public class WhenCreatingANewDyescapeBotConfigurationExceptionTest {

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
