package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto;

import java.util.Date;

public class EmailSequenceStepRequest {
    private String toEmailAddress;
    private String subject;
    private String bodyText;
    private long delayInSeconds;
    private boolean isCompleted;
    private Date scheduledAt;
    private String sequenceId;

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }

    public void setToEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public long getDelayInSeconds() {
        return delayInSeconds;
    }

    public void setDelayInSeconds(long delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Date getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Date scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    @Override
    public String toString() {
        return "EmailSequenceStepRequest{" +
                "sequenceId='" + sequenceId + '\'' +
                ", toEmailAddress='" + toEmailAddress + '\'' +
                ", subject='" + subject + '\'' +
                ", bodyText='" + bodyText + '\'' +
                ", delayInSeconds=" + delayInSeconds +
                ", isCompleted=" + isCompleted +
                ", scheduledAt=" + scheduledAt +
                '}';
    }

}
