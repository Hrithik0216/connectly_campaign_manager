package com.connectly_cm.Connectly_CM.buyContacts.model;

import com.connectly_cm.Connectly_CM.usersUtils.model.User;

import java.util.List;

public class PagedContactResponse {
    private List<Contacts> contacts;
    private int currentPage;
    private int totalContacts;

    public List<Contacts> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contacts> contacts) {
        this.contacts = contacts;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }



    public int getTotalContacts() {
        return totalContacts;
    }

    public void setTotalContacts(int totalContacts) {
        this.totalContacts = totalContacts;
    }


}
