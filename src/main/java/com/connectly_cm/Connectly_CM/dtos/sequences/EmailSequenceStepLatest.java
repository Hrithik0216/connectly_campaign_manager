package com.connectly_cm.Connectly_CM.dtos.sequences;

import com.connectly_cm.Connectly_CM.enums.SequenceStepStaus;

public class EmailSequenceStepLatest {
    private String toEmailAddress;
    private String subject;
    private String bodyText;
    private String stepScheduledAt;
    private String createdAt;
    private String updatedAt;
    private SequenceStepStaus stepStatus;

    public SequenceStepStaus getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(SequenceStepStaus stepStatus) {
        this.stepStatus = stepStatus;
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

    public String getStepScheduledAt() {
        return stepScheduledAt;
    }

    public void setStepScheduledAt(String stepScheduledAt) {
        this.stepScheduledAt = stepScheduledAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
