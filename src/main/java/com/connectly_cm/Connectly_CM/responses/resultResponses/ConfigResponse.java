package com.connectly_cm.Connectly_CM.responses.resultResponses;

public class ConfigResponse {
    private int statusCode;
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }


}
