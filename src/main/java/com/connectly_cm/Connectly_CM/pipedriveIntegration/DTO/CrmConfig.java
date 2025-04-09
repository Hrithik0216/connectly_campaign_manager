package com.connectly_cm.Connectly_CM.pipedriveIntegration.DTO;

public class CrmConfig {
    private CrmEntityConfig lead;
    private CrmEntityConfig contact;

    public CrmEntityConfig getLead() {
        return lead;
    }

    public void setLead(CrmEntityConfig lead) {
        this.lead = lead;
    }

    public CrmEntityConfig getContact() {
        return contact;
    }

    public void setContact(CrmEntityConfig contact) {
        this.contact = contact;
    }


}
