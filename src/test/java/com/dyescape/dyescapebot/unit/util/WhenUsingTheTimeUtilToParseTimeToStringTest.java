package com.dyescape.dyescapebot.unit.util;

import com.dyescape.dyescapebot.util.TimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When using the TimeUtil to parse time to string")
public class WhenUsingTheTimeUtilToParseTimeToStringTest {

    @Test
    @DisplayName("It should generate a correct string with 2d1h30m argument")
    public void itShouldGenerateACorrectStringWith2D1H30MArgument() {
        String argument = "2d1h30m";
        long punishmentTime = TimeUtil.parseFromRelativeString(argument);

        String result = TimeUtil.parsePunishmentTime(punishmentTime);

        assertEquals("2 days, 1 hour and 30 minutes", result);
    }

    @Test
    @DisplayName("It should generate a correct string with 1d2h1m argument")
    public void itShouldGenerateACorrectStringWith1D2H1MArgument() {

        String argument = "1d2h1m";
        long punishmentTime = TimeUtil.parseFromRelativeString(argument);
        String result = TimeUtil.parsePunishmentTime(punishmentTime);

        assertEquals("1 day, 2 hours and 1 minute", result);
    }

    @Test
    @DisplayName("It should generate a correct string with 2h1m argument")
    public void itShouldGenerateACorrectStringWith2H1MArgument() {

        String argument = "2h1m";
        long punishmentTime = TimeUtil.parseFromRelativeString(argument);
        String result = TimeUtil.parsePunishmentTime(punishmentTime);

        assertEquals("2 hours and 1 minute", result);
    }

    @Test
    @DisplayName("It should generate a correct string with 1h1m argument")
    public void itShouldGenerateACorrectStringWith1H1MArgument() {

        String argument = "1h1m";
        long punishmentTime = TimeUtil.parseFromRelativeString(argument);
        String result = TimeUtil.parsePunishmentTime(punishmentTime);

        assertEquals("1 hour and 1 minute", result);
    }

    @Test
    @DisplayName("It should generate a correct string with 1m argument")
    public void itShouldGenerateACorrectStringWith1MArgument() {

        String argument = "1m";
        long punishmentTime = TimeUtil.parseFromRelativeString(argument);
        String result = TimeUtil.parsePunishmentTime(punishmentTime);

        assertEquals("1 minute", result);
    }

    @Test
    @DisplayName("It should generate a correct string with 2m argument")
    public void itShouldGenerateACorrectStringWith2MArgument() {

        String argument = "2m";
        long punishmentTime = TimeUtil.parseFromRelativeString(argument);
        String result = TimeUtil.parsePunishmentTime(punishmentTime);

        assertEquals("2 minutes", result);
    }

    @Test
    @DisplayName("It should generate a correct string with 2h argument")
    public void itShouldGenerateACorrectStringWith2HArgument() {

        String argument = "2h";
        long punishmentTime = TimeUtil.parseFromRelativeString(argument);
        String result = TimeUtil.parsePunishmentTime(punishmentTime);

        assertEquals("2 hours", result);
    }
}
