package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto;

public class EmailResponse {
    public EmailResponse(String fromAddress, String toAddress) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
    }

    private String fromAddress;
    private String toAddress;
}
