package com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
public class UnifiedInboxAccounts {
    @Id
    private String id;
    private List<ConnectedGmailAccount> connectedEmailAccounts;
    private String userId;

    public String getId() {
        return id;
    }

    public List<ConnectedGmailAccount> getConnectedEmailAccounts() {
        return connectedEmailAccounts;
    }

    public void setConnectedEmailAccounts(List<ConnectedGmailAccount> connectedEmailAccounts) {
        this.connectedEmailAccounts = connectedEmailAccounts;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
