package com.dyescape.bot.discord.command.resolver.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("When validating an invalid user argument")
public class WhenValidatingAnInvalidUserArgumentTest {

    @Test
    @DisplayName("It should thrown an error")
    public void itShouldNotThrowAnError() {
        UserValidator validator = new UserValidator();
        validator.validate("<@!267965217412087818>");
        validator.validate("<@267965217412087818>");
        validator.validate("267965217412087818");
        validator.validate("MrDienns#0001");
    }
}
