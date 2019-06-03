package com.dyescape.dyescapebot.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtil {

    private static final Pattern PERIOD_PATTERN = Pattern.compile("([0-9]+)([dhm])");

    private TimeUtil() {

    }

    /**
     * Function used to parse time period strings into a long representing the instant epoch millisecond
     * time.
     * @param period String representing the relative time, for example "5h3m40s"
     * @return long
     * @author Dennis van der Veeke
     * @since 0.1.0
     */
    public static Long parseFromRelativeString(String period) {
        if(period == null) return null;
        period = period.toLowerCase(Locale.ENGLISH);
        Matcher matcher = PERIOD_PATTERN.matcher(period);
        Instant instant=Instant.EPOCH;
        while(matcher.find()){
            int num = Integer.parseInt(matcher.group(1));
            String typ = matcher.group(2);
            switch (typ) {
                case "h":
                    instant = instant.plus(Duration.ofHours(num));
                    break;
                case "d":
                    instant = instant.plus(Duration.ofDays(num));
                    break;
                case "m":
                    instant = instant.plusSeconds(num * 60);
                    break;
            }
        }
        return instant.toEpochMilli();
    }

    /**
     * Takes a time in milliseconds and parses it to a string which (humanly readable)
     * parses the time. Includes nice grammar features.
     * @param milliseconds
     * @return Parsed punishment time
     * @author Dennis van der Veeke
     * @since 0.1.0
     */
    public static String parsePunishmentTime(long milliseconds) {

        long secs = milliseconds / 1000;
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
}
