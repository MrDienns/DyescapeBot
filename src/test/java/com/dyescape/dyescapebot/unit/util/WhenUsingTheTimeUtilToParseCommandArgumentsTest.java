package com.dyescape.dyescapebot.unit.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.dyescape.dyescapebot.util.TimeUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When using the TimeUtil to parse command arguments")
public class WhenUsingTheTimeUtilToParseCommandArgumentsTest {

    @Test
    @DisplayName("It should correctly parse the argument into the correct time")
    public void itShouldCorrectlyParseTheArgumentIntoTheCorrectTime() {
        String argumentOne = "2d1h30m";
        LocalDateTime expectedOne = LocalDateTime.now(ZoneId.of("UTC")).plusDays(2).plusHours(1).plusMinutes(30);
        long punishmentTime = TimeUtil.parseFromRelativeString(argumentOne);
        LocalDateTime actualOne = Instant.ofEpochMilli(System.currentTimeMillis() + punishmentTime)
                .atZone(ZoneId.of("UTC")).toLocalDateTime();

        // One big comparison check will fail due to misalignment of milliseconds
        assertEquals(expectedOne.getYear(), actualOne.getYear());
        assertEquals(expectedOne.getMonth(), actualOne.getMonth());
        assertEquals(expectedOne.getDayOfYear(), actualOne.getDayOfYear());
        assertEquals(expectedOne.getHour(), actualOne.getHour());
        assertEquals(expectedOne.getMinute(), actualOne.getMinute());
    }
}
