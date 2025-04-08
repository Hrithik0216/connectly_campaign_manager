package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.utils;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.Timewindow;
import org.apache.log4j.Logger;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public final class TimeWindowUtils {
    private static final Logger LOGGER = Logger.getLogger(TimeWindowUtils.class);
    public static boolean isWithinTimeWindow(ZonedDateTime now, List<Timewindow> timeWindow) {
        LOGGER.info("window in utils is "+timeWindow.toString());
        for (Timewindow window : timeWindow) {
            LocalTime start = LocalTime.parse(window.getStart());
            LocalTime end = LocalTime.parse(window.getEnd());
            List<String> days = window.getDays();

            // Check if the current day is in the allowed days
            String currentDay = now.getDayOfWeek().toString();
            if (!days.contains(currentDay)) {
                continue; // Skip this window if the day doesn't match
            }

            // Check if the current time is within the window
            LocalTime currentTime = now.toLocalTime();
            if (!currentTime.isBefore(start) && !currentTime.isAfter(end)) {
                return true; // Within the time window
            }
        }
        return false;
    }
}
