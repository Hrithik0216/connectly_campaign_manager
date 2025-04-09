package com.connectly_cm.Connectly_CM.pipedriveIntegration.DTO;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class CrmEntityConfig {
    private String sourceId;
    private String sourceName;
    private Date lastSynced; // Zoho to salesgear import
    private boolean autoSyncEnabled;
    private boolean updateContact;
    private Set<String> tags;
    private PollingInterval pollingInterval;
    private Date nextPollingDate;
    private boolean syncNow; // Based on this dynamic sync will work
    private List<PollingCondition> conditions;
    private boolean addContactEnabled;
    private Long syncStartTime;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Date getLastSynced() {
        return lastSynced;
    }

    public void setLastSynced(Date lastSynced) {
        this.lastSynced = lastSynced;
    }

    public boolean isAutoSyncEnabled() {
        return autoSyncEnabled;
    }

    public void setAutoSyncEnabled(boolean autoSyncEnabled) {
        this.autoSyncEnabled = autoSyncEnabled;
    }

    public boolean isUpdateContact() {
        return updateContact;
    }

    public void setUpdateContact(boolean updateContact) {
        this.updateContact = updateContact;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public PollingInterval getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(PollingInterval pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public Date getNextPollingDate() {
        return nextPollingDate;
    }

    public void setNextPollingDate(Date nextPollingDate) {
        this.nextPollingDate = nextPollingDate;
    }

    public boolean isSyncNow() {
        return syncNow;
    }

    public void setSyncNow(boolean syncNow) {
        this.syncNow = syncNow;
    }

    public List<PollingCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<PollingCondition> conditions) {
        this.conditions = conditions;
    }

    public boolean isAddContactEnabled() {
        return addContactEnabled;
    }

    public void setAddContactEnabled(boolean addContactEnabled) {
        this.addContactEnabled = addContactEnabled;
    }

    public Long getSyncStartTime() {
        return syncStartTime;
    }

    public void setSyncStartTime(Long syncStartTime) {
        this.syncStartTime = syncStartTime;
    }

    //    private List<CrmField> fields;

}
