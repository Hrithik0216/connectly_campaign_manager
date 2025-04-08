package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.Timewindow;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Document("email_Sequence")
public class EmailSequence {
    @Id
    private String id;
    private String userId;
    private String fromAddress;
    private String threadId;

    public List<Timewindow> getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(List<Timewindow> timeWindow) {
        this.timeWindow = timeWindow;
    }

    private List<Timewindow> timeWindow;
    private boolean isActive;
    private Date createdAt;
    private Date lastProcessed;
    private List<EmailSequenceStep> emailSteps;

    public List<EmailSequenceStep> getEmailSteps() {
        return emailSteps;
    }

    public void setEmailSteps(List<EmailSequenceStep> emailSteps) {
        this.emailSteps = emailSteps;
    }



    public String getId() {
        return id;
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



    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastProcessed() {
        return lastProcessed;
    }

    public void setLastProcessed(Date lastProcessed) {
        this.lastProcessed = lastProcessed;
    }

    @Override
    public String toString() {
        return "EmailSequence{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", threadId='" + threadId + '\'' +
                ", timeWindow=" + timeWindow +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", lastProcessed=" + lastProcessed +
                ", emailSteps=" + emailSteps +
                '}';
    }

}
