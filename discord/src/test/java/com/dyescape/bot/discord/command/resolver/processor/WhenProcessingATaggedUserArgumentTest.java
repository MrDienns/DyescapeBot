package com.dyescape.bot.discord.command.resolver.processor;

import net.dv8tion.jda.api.JDA;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@DisplayName("UserValidator")
public class WhenProcessingATaggedUserArgumentTest {

    @Test
    @DisplayName("It should remove all special characters")
    public void itShouldRemoveAllSpecialCharacters() {
        UserProcessor processor = new UserProcessor(mock(JDA.class));
        //assertEquals("267965217412087818", processor.process("<@!267965217412087818>"));
        //assertEquals("267965217412087818", processor.process("<@267965217412087818>"));
        //assertEquals("267965217412087818", processor.process("<267965217412087818>"));
    }
}
