package com.dyescape.bot.discord.command.resolver.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UserValidator")
public class WhenProcessingATaggedUserArgumentTest {

    @Test
    @DisplayName("It should remove all special characters")
    public void itShouldRemoveAllSpecialCharacters() {
        UserProcessor processor = new UserProcessor();
        assertEquals("267965217412087818", processor.process("<@!267965217412087818>"));
        assertEquals("267965217412087818", processor.process("<@267965217412087818>"));
        assertEquals("267965217412087818", processor.process("<267965217412087818>"));
    }
}
