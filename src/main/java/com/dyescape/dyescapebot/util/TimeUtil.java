package com.dyescape.dyescapebot.util;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.time.LocalDateTime;

/**
 * Simple utility to perform time related tasks quickly.
 * @author Dennis van der Veeke
 */
public final class TimeUtil {

    private static final PeriodFormatter PERIOD_FORMATTER = new PeriodFormatterBuilder()
            .appendDays().appendSuffix("d")
            .appendDays().appendSuffix("day")
            .appendDays().appendSuffix("days")
            .appendHours().appendSuffix("h")
            .appendHours().appendSuffix("hour")
            .appendHours().appendSuffix("hours")
            .appendMinutes().appendSuffix("m")
            .appendMinutes().appendSuffix("minute")
            .appendMinutes().appendSuffix("minutes")
            .toFormatter();

    private TimeUtil() {

    }

    /**
     * Function used to parse time period strings into a {@link LocalDateTime}. The
     * returned {@link LocalDateTime} indicates when the punishment ends. The result
     * time is in UTC timezone.
     * @param relativeTime
     * @return {@link LocalDateTime}
     * @author Dennis van der Veeke
     */
    public static long parseFromRelativeString(String relativeTime) {
        Period period = PERIOD_FORMATTER.parsePeriod(relativeTime);
        return period.toStandardDuration().getMillis();
    }

    /**
     * Takes a time in milliseconds and parses it to a string which (humanly readable)
     * parses the time. Includes nice grammar features.
     * @param milliseconds
     * @return Parsed punishment time
     * @author Dennis van der Veeke
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
