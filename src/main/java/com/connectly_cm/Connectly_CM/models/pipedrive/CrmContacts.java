package com.connectly_cm.Connectly_CM.models.pipedrive;

import com.connectly_cm.Connectly_CM.dtos.pipedrive.CrmOwnerDetails;
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
    private CrmOwnerDetails ownerDetails;

    @Override
    public String toString() {
        return "CrmContacts{" +
                "id='" + id + '\'' +
                ", ownerDetails=" + ownerDetails +
                ", contactId=" + contactId +
                ", jobTitle='" + jobTitle + '\'' +
                ", phoneData=" + phoneData +
                ", status=" + status +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", orgName='" + orgName + '\'' +
                ", emailData=" + emailData +
                ", primaryEmail='" + primaryEmail + '\'' +
                ", companyId=" + companyId +
                ", createAt='" + createAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", importType='" + importType + '\'' +
                ", userId='" + userId + '\'' +
                ", postalAddressSubpremise='" + postalAddressSubpremise + '\'' +
                ", postalAddressLocality='" + postalAddressLocality + '\'' +
                ", postalAddressLong='" + postalAddressLong + '\'' +
                ", postalAddress='" + postalAddress + '\'' +
                '}';
    }

    private int contactId;
    private String jobTitle;
    private List<PhoneData> phoneData;
    private boolean status;
    private String firstName;
    private String lastName;
    private String orgName;
    private List<EmailData> emailData;
    private String primaryEmail;
    private int companyId;
    private String createAt;
    private String updatedAt;
    private String importType;
    private String userId;
    private String postalAddressSubpremise;
    private String postalAddressLocality;
    private String postalAddressLong;
    private String postalAddress;
    private String contactDataType;
    private int pipedriveLeadOwnerId;

    private int pipedriveLeadOrgId;

    public int getPipedriveLeadPersonId() {
        return pipedriveLeadPersonId;
    }

    public void setPipedriveLeadPersonId(int pipedriveLeadPersonId) {
        this.pipedriveLeadPersonId = pipedriveLeadPersonId;
    }

    private int pipedriveLeadPersonId;


    public int getPipedriveLeadOrgId() {
        return pipedriveLeadOrgId;
    }

    public void setPipedriveLeadOrgId(int pipedriveLeadOrgId) {
        this.pipedriveLeadOrgId = pipedriveLeadOrgId;
    }


    public int getPipedriveLeadOwnerId() {
        return pipedriveLeadOwnerId;
    }

    public void setPipedriveLeadOwnerId(int pipedriveLeadOwnerId) {
        this.pipedriveLeadOwnerId = pipedriveLeadOwnerId;
    }

    public String getPipedriveLeadId() {
        return pipedriveLeadId;
    }

    public void setPipedriveLeadId(String pipedriveLeadId) {
        this.pipedriveLeadId = pipedriveLeadId;
    }

    private String pipedriveLeadId;


    public String getContactDataType() {
        return contactDataType;
    }

    public void setContactDataType(String contactDataType) {
        this.contactDataType = contactDataType;
    }

    public String getPostalAddressSubpremise() {
        return postalAddressSubpremise;
    }

    public void setPostalAddressSubpremise(String postalAddressSubpremise) {
        this.postalAddressSubpremise = postalAddressSubpremise;
    }

    public String getPostalAddressLocality() {
        return postalAddressLocality;
    }

    public void setPostalAddressLocality(String postalAddressLocality) {
        this.postalAddressLocality = postalAddressLocality;
    }

    public String getPostalAddressLong() {
        return postalAddressLong;
    }

    public void setPostalAddressLong(String postalAddressLong) {
        this.postalAddressLong = postalAddressLong;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }


    public CrmOwnerDetails getOwnerDetails() {
        return ownerDetails;
    }

    public void setOwnerDetails(CrmOwnerDetails ownerDetails) {
        this.ownerDetails = ownerDetails;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
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
