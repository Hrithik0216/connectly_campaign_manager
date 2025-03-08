package com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.model;

import java.util.Date;
import java.util.List;

public class ConnectedGmailAccount {
    private List<String> scopes;
    private String connectedMail;

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

    public String getConnectedMail() {
        return connectedMail;
    }

    public void setConnectedMail(String connectedMail) {
        this.connectedMail = connectedMail;
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
