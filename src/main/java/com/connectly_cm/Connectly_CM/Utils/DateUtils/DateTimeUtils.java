package com.connectly_cm.Connectly_CM.Utils.DateUtils;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {
    private static final Logger LOGGER = Logger.getLogger(DateTimeUtils.class);
    private static final String DATE_FORMAT="dd-M-yyyy HH:mm:ss";

    public static final String convertDateToString(Date date, TimeZone timeZone, Integer seconds) {
        if (date == null) {
            LOGGER.warn("The date is null");
            throw new IllegalArgumentException("The date is null");
        }
        if (timeZone == null) {
            LOGGER.warn("The timeZone is null");
            throw new IllegalArgumentException("The timeZone is null");
        }
        String dateStr = null;
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        df.setTimeZone(timeZone);
        try {
            Date modifiedDate = (Date) date.clone();
            if (seconds != null) {
                long finalMilliSec = (seconds * 1000L) + modifiedDate.getTime();
                modifiedDate.setTime(finalMilliSec);
            }
            dateStr = df.format(modifiedDate);
            LOGGER.info("Converted date to String is " + dateStr);
        } catch (Exception ex) {
            LOGGER.error("Exception in converting date to String: " + ex.getMessage());
            ex.printStackTrace();
        }
        return dateStr;
    }

    public static Date convertDateStringTODate(String stringDate) {
        if (stringDate == null) {
            LOGGER.warn("Input stringDate or format is null");
            throw new IllegalArgumentException("The stringDate is null");
        }
        Date date = null;
        try {
            DateFormat iso8601 = new SimpleDateFormat(DATE_FORMAT);
            date = iso8601.parse(stringDate);
        } catch (ParseException e) {
            LOGGER.error("Error occurred while converting given date string to Date dataType " + e.getMessage());
            throw new IllegalArgumentException("Invalid date format: " + stringDate, e);
        }
        return date;
    }
}
