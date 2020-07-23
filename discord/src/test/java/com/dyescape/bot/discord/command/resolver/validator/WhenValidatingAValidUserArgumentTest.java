package com.dyescape.bot.discord.command.resolver.validator;

import co.aikar.commands.InvalidCommandArgument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("When validating a valid user argument")
public class WhenValidatingAValidUserArgumentTest {

    @Test
    @DisplayName("It should not thrown an error")
    public void itShouldNotThrowAnError() {
        UserValidator validator = new UserValidator();
        assertThrows(InvalidCommandArgument.class, () -> {
            validator.validate("MrDienns");
        });
        assertThrows(InvalidCommandArgument.class, () -> {
            validator.validate("MrDienns#0001");
        });
    }
}
