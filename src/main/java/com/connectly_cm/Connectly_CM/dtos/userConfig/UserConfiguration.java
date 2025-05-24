package com.connectly_cm.Connectly_CM.dtos.userConfig;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.Timewindow;

import java.util.List;

public class UserConfiguration {
    private String fromAddress;
    private List<Timewindow> timeWindow;

    public long getDelayInSeconds() {
        return delayInSeconds;
    }

    public void setDelayInSeconds(long delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }

    private long delayInSeconds;
    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public List<Timewindow> getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(List<Timewindow> timeWindow) {
        this.timeWindow = timeWindow;
    }
}
