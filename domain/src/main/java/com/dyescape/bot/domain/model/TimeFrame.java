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

    public String asMessage() {
        long secs = this.getDuration().toMillis() / 1000;
        long mins = secs / 60;
        long hours = mins / 60;
        long days = hours / 24;

        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            builder.append(days);
            builder.append(days == 1 ? " day" : " days");
        }

        if (hours % 24 != 0) {
            if (!builder.toString().isEmpty()) {
                builder.append(", ");
            }

            builder.append(hours % 24);
            builder.append(hours % 24 == 1 ? " hour" : " hours");
        }

        if (mins % 60 != 0) {
            if (!builder.toString().isEmpty()) {
                builder.append(", ");
            }

            builder.append(mins % 60);
            builder.append(mins % 60 == 1 ? " minute" : " minutes");
        }

        String result = builder.toString();

        int replaceIndex = result.lastIndexOf(", ");

        if (replaceIndex == -1) {
            return result;
        } else {
            return result.substring(0, replaceIndex) + " and " +
                    result.substring(replaceIndex + ", ".length());
        }
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
