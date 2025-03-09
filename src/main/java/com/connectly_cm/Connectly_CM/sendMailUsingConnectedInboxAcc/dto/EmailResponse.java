package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailResponse {

    @JsonProperty("fromEmail")
    private String fromEmail;

    @JsonProperty("toEmail")
    private String toEmail;

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    @JsonProperty("threadId")
    private String threadId;

    public EmailResponse() {
    }

    public EmailResponse(String fromEmail, String toEmail, String threadId) {
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
        this.threadId = threadId;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String getToEmail() {
        return toEmail;
    }
}
