package com.connectly_cm.Connectly_CM.utils.DateUtils;

import org.apache.log4j.Logger;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {
    private static final Logger LOGGER = Logger.getLogger(DateTimeUtils.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT)
            .withZone(ZoneId.of("UTC"));

    public static final String convertDateToString(Date date, TimeZone timeZone, Integer seconds) {
        if (date == null) {
            LOGGER.warn("The date is null");
            throw new IllegalArgumentException("The date is null");
        }
        if (timeZone == null) {
            LOGGER.warn("The timeZone is null");
            throw new IllegalArgumentException("The timeZone is null");
        }

        try {
            Instant instant = date.toInstant();
            if (seconds != null) {
                instant = instant.plusSeconds(seconds);
            }

            ZonedDateTime zonedDateTime = instant.atZone(timeZone.toZoneId());
            String dateStr = zonedDateTime.format(ISO_FORMATTER);
            LOGGER.info("Converted date to String is " + dateStr);
            return dateStr;
        } catch (Exception ex) {
            LOGGER.error("Exception in converting date to String: " + ex.getMessage(), ex);
            throw new IllegalArgumentException("Date conversion failed", ex);
        }
    }

    public static Date convertDateStringTODate(String stringDate) {
        if (stringDate == null) {
            LOGGER.warn("Input stringDate is null");
            throw new IllegalArgumentException("The stringDate is null");
        }

        try {
            Instant instant = Instant.from(ISO_FORMATTER.parse(stringDate));
            return Date.from(instant);
        } catch (DateTimeParseException e) {
            LOGGER.error("Error parsing date string: " + e.getMessage(), e);
            throw new IllegalArgumentException("Invalid date format: " + stringDate, e);
        }
    }

    public static final String convertDateToStringWithOffset(Date date, TimeZone timeZone, Long seconds) {
        if (date == null) {
            LOGGER.warn("The date is null");
            throw new IllegalArgumentException("The date is null");
        }
        if (timeZone == null) {
            LOGGER.warn("The timeZone is null");
            throw new IllegalArgumentException("The timeZone is null");
        }

        try {
            Instant instant = date.toInstant();
            if (seconds != null) {
                instant = instant.plusSeconds(seconds);
            }

            ZonedDateTime zonedDateTime = instant.atZone(timeZone.toZoneId());
            String dateStr = zonedDateTime.format(ISO_FORMATTER);
            LOGGER.info("Converted date to String is " + dateStr);
            return dateStr;
        } catch (Exception ex) {
            LOGGER.error("Exception in converting date to String: " + ex.getMessage(), ex);
            throw new IllegalArgumentException("Date conversion failed", ex);
        }
    }

}