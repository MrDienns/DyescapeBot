package com.dyescape.bot.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TimeFrame object test")
public class TimeFrameTest {

    @Test
    @DisplayName("Test single period")
    public void testSinglePeriod() {
        Duration expected = Duration.ofMinutes(5);
        TimeFrame timeFrame = new TimeFrame("5m");
        assertEquals(timeFrame.getDuration(), expected, "TimeFrame did not create the right Duration");
    }

    @Test
    @DisplayName("Test multiple periods")
    public void testMultiplePeriods() {
        Duration expected = Duration.ZERO;
        expected = expected.plusDays(7);
        expected = expected.plusHours(30);
        expected = expected.plusMinutes(5);

        TimeFrame timeFrame = new TimeFrame("7d30h5m");
        assertEquals(timeFrame.getDuration(), expected, "TimeFrame did not create the right Duration");
    }
}
