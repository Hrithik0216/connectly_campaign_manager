package com.connectly_cm.Connectly_CM.models.pipedrive;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("crm_settings")
public class CrmSettings {
    @Id
    private String id;
    private String type;
    private String accessToken;
    private String refreshToken;
    private String apiDomain;
    private String createTs;
    private String updateTs;
    private String tokenType;
    private String userId;
    private String accessTokenExpiryDate;
    private String connectedEmail;
    private long apiLimitPerDay = 15000;

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    private List<String> scopes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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


    public String getApiDomain() {
        return apiDomain;
    }

    public void setApiDomain(String apiDomain) {
        this.apiDomain = apiDomain;
    }

    public String getAccessTokenExpiryDate() {
        return accessTokenExpiryDate;
    }

    public void setAccessTokenExpiryDate(String accessTokenExpiryDate) {
        this.accessTokenExpiryDate = accessTokenExpiryDate;
    }

    public String getCreateTs() {
        return createTs;
    }

    public void setCreateTs(String createTs) {
        this.createTs = createTs;
    }

    public String getUpdateTs() {
        return updateTs;
    }

    public void setUpdateTs(String updateTs) {
        this.updateTs = updateTs;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConnectedEmail() {
        return connectedEmail;
    }

    public void setConnectedEmail(String connectedEmail) {
        this.connectedEmail = connectedEmail;
    }


    public long getApiLimitPerDay() {
        return apiLimitPerDay;
    }

    public void setApiLimitPerDay(long apiLimitPerDay) {
        this.apiLimitPerDay = apiLimitPerDay;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

}
