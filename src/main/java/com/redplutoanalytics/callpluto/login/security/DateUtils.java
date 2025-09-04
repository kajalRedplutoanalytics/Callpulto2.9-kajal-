package com.redplutoanalytics.callpluto.login.security;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateUtils {

    public static LocalDateTime[] getDateRange(String timeFilter) {
        LocalDateTime now = LocalDateTime.now();
        return switch (timeFilter.toLowerCase()) {
            case "today" -> new LocalDateTime[]{now.with(LocalTime.MIN), now};
            case "yesterday" -> {
                LocalDateTime yesterday = now.minusDays(1);
                yield new LocalDateTime[]{yesterday.with(LocalTime.MIN), yesterday.with(LocalTime.MAX)};
            }
            case "week" -> new LocalDateTime[]{now.minusWeeks(1), now};
            case "month" -> new LocalDateTime[]{now.minusMonths(1), now};
            default -> throw new IllegalArgumentException("Invalid time filter: " + timeFilter);
        };
    }

    public static LocalDateTime[] getPreviousRange(String timeFilter) {
        LocalDateTime[] currentRange = getDateRange(timeFilter);
        LocalDateTime end = currentRange[0];
        LocalDateTime start = end.minus(java.time.Duration.between(currentRange[0], currentRange[1]));
        return new LocalDateTime[]{start, end};
    }

    public static LocalDate[] getDateRangeAsDate(String timeFilter) {
        LocalDateTime[] dateTimeRange = getDateRange(timeFilter);
        return new LocalDate[]{
            dateTimeRange[0].toLocalDate(),
            dateTimeRange[1].toLocalDate()
        };
    }
}
