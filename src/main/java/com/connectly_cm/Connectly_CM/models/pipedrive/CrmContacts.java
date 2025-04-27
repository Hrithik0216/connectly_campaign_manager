package com.connectly_cm.Connectly_CM.models.pipedrive;

import com.connectly_cm.Connectly_CM.dtos.pipedrive.EmailData;
import com.connectly_cm.Connectly_CM.dtos.pipedrive.PhoneData;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("crm_contacts")
public class CrmContacts {
    @Id
    private String id;
    //    private long emailMessagesCount
    private int ownerId;
    private String ownerName;
    private String ownerMail;
    private int contactId;
    private String jobTitle;
    private List<PhoneData> phoneData;
    private String status;
    private String firstName;
    private String orgName;
    private List<EmailData> emailData;
    private String primaryEmail;
    private int companyId;
    private String createAt;
    private String updatedAt;
    private String importType;
    private String userId;

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerMail() {
        return ownerMail;
    }

    public void setOwnerMail(String ownerMail) {
        this.ownerMail = ownerMail;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public List<PhoneData> getPhoneData() {
        return phoneData;
    }

    public void setPhoneData(List<PhoneData> phoneData) {
        this.phoneData = phoneData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public List<EmailData> getEmailData() {
        return emailData;
    }

    public void setEmailData(List<EmailData> emailData) {
        this.emailData = emailData;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
