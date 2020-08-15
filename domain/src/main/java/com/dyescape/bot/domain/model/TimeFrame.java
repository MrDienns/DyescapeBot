package com.dyescape.bot.domain.model;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeFrame {

    private static final Pattern PATTERN = Pattern.compile("([0-9]*)([dhm])");

    private final String originalFormat;
    private final Duration duration;

    public TimeFrame(String format) {
        this.originalFormat = format;
        Duration duration = Duration.ZERO;
        Matcher matcher = PATTERN.matcher(format);

        while (matcher.find()) {
            String matchNumber = matcher.group(1);
            String matchUnit = matcher.group(2);

            duration = appendToDuration(duration, Integer.parseInt(matchNumber), matchUnit);
        }

        this.duration = duration;
    }

    public String getOriginalFormat() {
        return this.originalFormat;
    }

    public Duration getDuration() {
        return this.duration;
    }

    @Override
    public String toString() {
        return this.getOriginalFormat();
    }

    private Duration appendToDuration(Duration duration, int quantity, String unit) {
        unit = unit.toLowerCase();
        switch (unit) {
            case "d":
                return duration.plusDays(quantity);
            case "h":
                return duration.plusHours(quantity);
            case "m":
                return duration.plusMinutes(quantity);
            default:
                return duration;
        }
    }
}
