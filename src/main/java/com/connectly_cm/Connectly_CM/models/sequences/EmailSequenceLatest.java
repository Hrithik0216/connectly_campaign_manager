package com.connectly_cm.Connectly_CM.models.sequences;

import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceStepLatest;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.Timewindow;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("email_sequence_latest")
public class EmailSequenceLatest {
    @Id
    private String id;
    private String sequenceName;
    private String userId;
    private String fromAddress;
    private List<Timewindow> timeWindow;
    private boolean isActive;
    private String createdAt;
    private String lastStepProcessedAt;
    private List<EmailSequenceStepLatest> emailSteps;
    private Long delayInSeconds;

    public Long getDelayInSeconds() {
        return delayInSeconds;
    }

    public void setDelayInSeconds(Long delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<Timewindow> getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(List<Timewindow> timeWindow) {
        this.timeWindow = timeWindow;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastStepProcessedAt() {
        return lastStepProcessedAt;
    }

    public void setLastStepProcessedAt(String lastStepProcessedAt) {
        this.lastStepProcessedAt = lastStepProcessedAt;
    }

    public List<EmailSequenceStepLatest> getEmailSteps() {
        return emailSteps;
    }

    public void setEmailSteps(List<EmailSequenceStepLatest> emailSteps) {
        this.emailSteps = emailSteps;
    }

    @Override
    public String toString() {
        return "EmailSequenceLatest{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", timeWindow=" + timeWindow +
                ", isActive=" + isActive +
                ", createdAt='" + createdAt + '\'' +
                ", lastStepProcessedAt='" + lastStepProcessedAt + '\'' +
                ", emailSteps=" + emailSteps +
                '}';
    }


}
