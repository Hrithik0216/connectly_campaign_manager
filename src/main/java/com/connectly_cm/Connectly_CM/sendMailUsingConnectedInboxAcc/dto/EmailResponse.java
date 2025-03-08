package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailResponse {

    @JsonProperty("fromEmail")
    private String fromEmail;

    @JsonProperty("toEmail")
    private String toEmail;

    public EmailResponse() {
    }

    public EmailResponse(String fromEmail, String toEmail) {
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String getToEmail() {
        return toEmail;
    }
}
