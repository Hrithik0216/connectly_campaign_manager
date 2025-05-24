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
    private String userId;
    private String fromAddress;
    private String threadId;
    private List<Timewindow> timeWindow;
    private boolean isActive;
    private String createdAt;
    private String lastProcessed;
    private List<EmailSequenceStepLatest> emailSteps;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastProcessed() {
        return lastProcessed;
    }

    public void setLastProcessed(String lastProcessed) {
        this.lastProcessed = lastProcessed;
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

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
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

    public List<EmailSequenceStepLatest> getEmailSteps() {
        return emailSteps;
    }

    public void setEmailSteps(List<EmailSequenceStepLatest> emailSteps) {
        this.emailSteps = emailSteps;
    }

}
