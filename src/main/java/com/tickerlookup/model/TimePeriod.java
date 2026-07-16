package com.tickerlookup.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Selectable lookup windows. Each period knows how to build the Yahoo Finance
 * chart query it needs and how the chart's time axis should be labelled.
 */
public enum TimePeriod {
    DAY("Last 24h", "HH:mm"),
    FIVE_DAY("Last 5 Days", "MM/dd HH:mm"),
    THIRTY_DAY("Last 30 Days", "MM/dd"),
    MTD("Month to Date", "MM/dd"),
    YTD("Year to Date", "MM/dd");

    private final String label;
    private final String axisPattern;

    TimePeriod(String label, String axisPattern) {
        this.label = label;
        this.axisPattern = axisPattern;
    }

    /** Human-readable name used in chart/stat headings. */
    public String label() {
        return label;
    }

    /** Formatter for the chart's time axis, appropriate to this period's granularity. */
    public DateTimeFormatter axisFormatter() {
        return DateTimeFormatter.ofPattern(axisPattern).withZone(ZoneId.systemDefault());
    }

    /**
     * The query string (everything after {@code ?}) for the Yahoo Finance chart
     * endpoint. Standard ranges use {@code range=}; month-to-date has no standard
     * token so it is expressed with explicit {@code period1}/{@code period2} bounds.
     */
    public String urlQuery() {
        return switch (this) {
            case DAY -> "interval=15m&range=1d";
            case FIVE_DAY -> "interval=30m&range=5d";
            case THIRTY_DAY -> "interval=1d&range=1mo";
            case YTD -> "interval=1d&range=ytd";
            case MTD -> {
                ZoneId zone = ZoneId.systemDefault();
                ZonedDateTime now = ZonedDateTime.now(zone);
                long period2 = now.toEpochSecond();
                long period1 = now.toLocalDate().withDayOfMonth(1).atStartOfDay(zone).toEpochSecond();
                yield "interval=1d&period1=" + period1 + "&period2=" + period2;
            }
        };
    }
}
