package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class EmailSequenceRequest {
    private String userId;
    private String fromAddress;
    private List<Timewindow> timeWindow;

    public List<Timewindow> getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(List<Timewindow> timeWindow) {
        this.timeWindow = timeWindow;
    }

    private List<EmailSequenceStepRequest> emailSteps;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }


    public List<EmailSequenceStepRequest> getEmailSteps() {
        return emailSteps;
    }

    public void setEmailSteps(List<EmailSequenceStepRequest> emailSteps) {
        this.emailSteps = emailSteps;
    }

    @Override
    public String toString() {
        return "EmailSequenceRequest{" +
                "userId='" + userId + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", timeWindow=" + (timeWindow != null ? timeWindow.toString() : "[]") +  // ✅ Ensures proper printing
                ", emailSteps=" + (emailSteps != null ? emailSteps.toString() : "[]") +
                '}';
    }


}
