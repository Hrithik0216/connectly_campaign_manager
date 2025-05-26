package com.connectly_cm.Connectly_CM.models.sequences;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.Timewindow;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("user_config")
public class UsersConfig {
    @Id
    private String id;
    private String userId;
    private String fromAddress;
    private List<Timewindow> timeWindow;
    private long delayInSeconds;

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
        return this.fromAddress;
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

    public long getDelayInSeconds() {
        return delayInSeconds;
    }

    public void setDelayInSeconds(long delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }
}
