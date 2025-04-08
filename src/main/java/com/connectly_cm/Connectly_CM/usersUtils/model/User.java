package com.connectly_cm.Connectly_CM.usersUtils.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String phoneNumber;
    private List<String> roles;
    private boolean isBasicEnabled;
    private boolean isProENabled;
    private boolean isEnterpriseEnabled;
    private long yearlyEmailCredits;
    private int monthlyEmailCredits;
    private long yearlyPhoneCredits;
    private int monthlyPhoneCredits;
    private long yearlyLinkedinCredits;
    private int monthlyLinkedinCredits;
    private int noOfEmailsPerMonth;
    private int noOfEmailsPerYear;
    private boolean userStatus;
    private boolean isYearlyPlanActive;
    private boolean isEmailValidationEnabled;
    private boolean zohoIntegrationEnabled;
    private boolean hubspotIntegrationEnabled;
    private int noOfSequencecs;
    private String nextBillingCycle;
    private String autoLoginKey;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isBasicEnabled() {
        return isBasicEnabled;
    }

    public void setBasicEnabled(boolean basicEnabled) {
        isBasicEnabled = basicEnabled;
    }

    public boolean isProENabled() {
        return isProENabled;
    }

    public void setProENabled(boolean proENabled) {
        isProENabled = proENabled;
    }

    public boolean isEnterpriseEnabled() {
        return isEnterpriseEnabled;
    }

    public void setEnterpriseEnabled(boolean enterpriseEnabled) {
        isEnterpriseEnabled = enterpriseEnabled;
    }

    public long getYearlyEmailCredits() {
        return yearlyEmailCredits;
    }

    public void setYearlyEmailCredits(long yearlyEmailCredits) {
        this.yearlyEmailCredits = yearlyEmailCredits;
    }

    public int getMonthlyEmailCredits() {
        return monthlyEmailCredits;
    }

    public void setMonthlyEmailCredits(int monthlyEmailCredits) {
        this.monthlyEmailCredits = monthlyEmailCredits;
    }

    public long getYearlyPhoneCredits() {
        return yearlyPhoneCredits;
    }

    public void setYearlyPhoneCredits(long yearlyPhoneCredits) {
        this.yearlyPhoneCredits = yearlyPhoneCredits;
    }

    public int getMonthlyPhoneCredits() {
        return monthlyPhoneCredits;
    }

    public void setMonthlyPhoneCredits(int monthlyPhoneCredits) {
        this.monthlyPhoneCredits = monthlyPhoneCredits;
    }

    public long getYearlyLinkedinCredits() {
        return yearlyLinkedinCredits;
    }

    public void setYearlyLinkedinCredits(long yearlyLinkedinCredits) {
        this.yearlyLinkedinCredits = yearlyLinkedinCredits;
    }

    public int getMonthlyLinkedinCredits() {
        return monthlyLinkedinCredits;
    }

    public void setMonthlyLinkedinCredits(int monthlyLinkedinCredits) {
        this.monthlyLinkedinCredits = monthlyLinkedinCredits;
    }

    public int getNoOfEmailsPerMonth() {
        return noOfEmailsPerMonth;
    }

    public void setNoOfEmailsPerMonth(int noOfEmailsPerMonth) {
        this.noOfEmailsPerMonth = noOfEmailsPerMonth;
    }

    public int getNoOfEmailsPerYear() {
        return noOfEmailsPerYear;
    }

    public void setNoOfEmailsPerYear(int noOfEmailsPerYear) {
        this.noOfEmailsPerYear = noOfEmailsPerYear;
    }

    public boolean isUserStatus() {
        return userStatus;
    }

    public void setUserStatus(boolean userStatus) {
        this.userStatus = userStatus;
    }

    public boolean isYearlyPlanActive() {
        return isYearlyPlanActive;
    }

    public void setYearlyPlanActive(boolean yearlyPlanActive) {
        isYearlyPlanActive = yearlyPlanActive;
    }

    public boolean isEmailValidationEnabled() {
        return isEmailValidationEnabled;
    }

    public void setEmailValidationEnabled(boolean emailValidationEnabled) {
        isEmailValidationEnabled = emailValidationEnabled;
    }

    public boolean isZohoIntegrationEnabled() {
        return zohoIntegrationEnabled;
    }

    public void setZohoIntegrationEnabled(boolean zohoIntegrationEnabled) {
        this.zohoIntegrationEnabled = zohoIntegrationEnabled;
    }

    public boolean isHubspotIntegrationEnabled() {
        return hubspotIntegrationEnabled;
    }

    public void setHubspotIntegrationEnabled(boolean hubspotIntegrationEnabled) {
        this.hubspotIntegrationEnabled = hubspotIntegrationEnabled;
    }

    public int getNoOfSequencecs() {
        return noOfSequencecs;
    }

    public void setNoOfSequencecs(int noOfSequencecs) {
        this.noOfSequencecs = noOfSequencecs;
    }

    public String getNextBillingCycle() {
        return nextBillingCycle;
    }

    public void setNextBillingCycle(String nextBillingCycle) {
        this.nextBillingCycle = nextBillingCycle;
    }

    public String getAutoLoginKey() {
        return autoLoginKey;
    }

    public void setAutoLoginKey(String autoLoginKey) {
        this.autoLoginKey = autoLoginKey;
    }


}
