package com.connectly_cm.Connectly_CM.responses;

public class CreateSequenceResponse {
    public CreateSequenceResponse(int statusCode, String errMessage) {
        this.statusCode = statusCode;
        this.errMessage = errMessage;
    }

    private int statusCode;
    private String errMessage;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
