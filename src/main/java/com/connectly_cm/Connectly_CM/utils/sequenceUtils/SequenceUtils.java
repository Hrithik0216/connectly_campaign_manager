package com.connectly_cm.Connectly_CM.utils.sequenceUtils;

import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceStepLatest;
import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.Timewindow;
import com.connectly_cm.Connectly_CM.utils.DateUtils.DateTimeUtils;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SequenceUtils {
    private static ZonedDateTime alignWithTimeWindow(ZonedDateTime time, List<Timewindow> timeWindows) {
        while (true) {
            String currentDay = time.getDayOfWeek().name().toUpperCase(); // MONDAY, TUESDAY...
            boolean validDay = false;

            for (Timewindow window : timeWindows) {
                if (!window.contains(currentDay)) continue;

                LocalTime start = LocalTime.parse(window.getStart());
                LocalTime end = LocalTime.parse(window.getEnd());
                LocalTime currentTime = time.toLocalTime();

                // If current time is within the allowed range
                if (!currentTime.isBefore(start) && !currentTime.isAfter(end)) {
                    return time;
                }

                // If before the window, adjust to today at start time
                if (currentTime.isBefore(start)) {
                    return ZonedDateTime.of(time.toLocalDate(), start, time.getZone());
                }

                // If after the window, try the next day
                validDay = true;
            }

            // Move to next day at first available time
            time = time.plusDays(1).with(LocalTime.MIN);
        }
    }

    public static void scheduleSteps(List<EmailSequenceStepLatest> steps, UsersConfig usersConfig){
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        List<Timewindow> timeWindow = usersConfig.getTimeWindow();
        ZoneId zoneId = ZoneId.of("UTC"); // Adjust if needed
        long delay = usersConfig.getDelayInSeconds();
        ZonedDateTime current = ZonedDateTime.now(zoneId);
        for (int i = 0; i < steps.size(); i++) {
            ZonedDateTime targetTime = current.plusSeconds(i * delay);
            targetTime = alignWithTimeWindow(targetTime, timeWindow);

            EmailSequenceStepLatest step = steps.get(i);
            step.setStepScheduledAt(DateTimeUtils.convertDateToStringWithOffset(
                    Date.from(targetTime.toInstant()),
                    TimeZone.getTimeZone("UTC"),
                    null
            ));
        }
    }
}
