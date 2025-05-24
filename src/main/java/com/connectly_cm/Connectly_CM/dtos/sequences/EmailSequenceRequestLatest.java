package com.connectly_cm.Connectly_CM.dtos.sequences;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.EmailSequenceStepRequest;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.Timewindow;

import java.util.List;

public class EmailSequenceRequestLatest {
    private String fromAddress;
    private List<Timewindow> timeWindow;

    public List<Timewindow> getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(List<Timewindow> timeWindow) {
        this.timeWindow = timeWindow;
    }

    private List<EmailSequenceStepRequest> emailSteps;

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
                ", fromAddress='" + fromAddress + '\'' +
                ", timeWindow=" + (timeWindow != null ? timeWindow.toString() : "[]") +  // ✅ Ensures proper printing
                ", emailSteps=" + (emailSteps != null ? emailSteps.toString() : "[]") +
                '}';
    }
}
