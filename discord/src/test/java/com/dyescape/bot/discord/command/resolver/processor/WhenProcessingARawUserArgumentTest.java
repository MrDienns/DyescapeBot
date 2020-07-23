package com.dyescape.bot.discord.command.resolver.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When processing a raw user argument")
public class WhenProcessingARawUserArgumentTest {

    @Test
    @DisplayName("It should leave the argument as is")
    public void itShouldNotThrowAnError() {
        UserProcessor processor = new UserProcessor();
        assertEquals("267965217412087818", processor.process("267965217412087818"));
    }
}
