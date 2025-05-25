package com.connectly_cm.Connectly_CM.dtos.userConfig;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.Timewindow;

import java.util.List;

public class UserConfiguration {
    private List<Timewindow> timeWindow;
    private Long delayInSeconds;

    public Long getDelayInSeconds() {
        return delayInSeconds;
    }

    public void setDelayInSeconds(Long delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }

    public List<Timewindow> getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(List<Timewindow> timeWindow) {
        this.timeWindow = timeWindow;
    }
}
