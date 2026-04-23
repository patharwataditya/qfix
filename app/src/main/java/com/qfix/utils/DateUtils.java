package com.qfix.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault());

    /**
     * Formats a date to a readable string
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * Formats a date with time to a readable string
     */
    public static String formatDateTime(Date date) {
        return DATETIME_FORMAT.format(date);
    }

    /**
     * Calculates the time difference between two dates and returns a human-readable string
     */
    public static String getTimeAgo(Date startDate, Date endDate) {
        long duration = endDate.getTime() - startDate.getTime();
        
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        duration -= TimeUnit.DAYS.toMillis(days);
        
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toMillis(hours);
        
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        
        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else {
            return "Just now";
        }
    }

    /**
     * Checks if a date is today
     */
    public static boolean isToday(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(date).equals(sdf.format(new Date()));
    }

    /**
     * Checks if a date is yesterday
     */
    public static boolean isYesterday(Date date) {
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date yesterday = new Date(System.currentTimeMillis() - DAY_IN_MS);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(date).equals(sdf.format(yesterday));
    }
}