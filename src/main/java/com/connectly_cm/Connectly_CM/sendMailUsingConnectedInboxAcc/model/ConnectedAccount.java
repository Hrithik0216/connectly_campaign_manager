package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
public class ConnectedAccount {

    @Id
    private String id;
    private List<String> scopes;
    private List<String> connectedMails;
    private String userId;
    private String accessToken;
    private String refreshToken;
    private Date timestamp;
    private Date tokenExpiryTime;

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public List<String> getConnectedMails() {
        return connectedMails;
    }

    public void setConnectedMails(List<String> connectedMails) {
        this.connectedMails = connectedMails;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTokenExpiryTime() {
        return tokenExpiryTime;
    }

    public void setTokenExpiryTime(Date tokenExpiryTime) {
        this.tokenExpiryTime = tokenExpiryTime;
    }
}
