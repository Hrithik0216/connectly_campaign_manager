package com.connectly_cm.Connectly_CM.ErrResponses;

import org.json.JSONObject;

public class CrmErrResponse {
    private String responseCode;
    private String message;

    public CrmErrResponse(String responseCode, String message) {
        this.responseCode = responseCode;
        this.message = message;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
