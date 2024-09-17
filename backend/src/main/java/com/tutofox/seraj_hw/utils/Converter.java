package com.tutofox.seraj_hw.utils;

/**
 * Utility class for performing time unit conversions.
 * Provides methods to convert minutes to milliseconds and hours to days.
 */
public class Converter {

    public static final int SECONDS_PER_MINUTE = 60;
    public static final long MILLISECONDS_PER_SECOND = 1000L;
    public static final int HOURS_PER_DAY = 24;


    /**
     * Converts a given duration in minutes to milliseconds.
     *
     * @param minutes the duration in minutes
     * @return the equivalent duration in milliseconds
     */
    public static long minutesToMilliseconds(int minutes) {
        return minutes * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND; // 60 seconds per minute, 1000 milliseconds per second
    }

    /**
     * Converts a given duration in hours to days.
     *
     * @param hours the duration in hours
     * @return the equivalent duration in days
     */
    public static int daysToHours(int hours) {
        return hours * HOURS_PER_DAY;
    }
}
