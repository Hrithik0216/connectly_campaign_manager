package com.connectly_cm.Connectly_CM.responses.exceptionResponses;

import java.util.Date;

public class ExceptionResponse {
    private Date date;
    private String error;

    public ExceptionResponse(Date date, String error, String message) {
        this.date = date;
        this.error = error;
        this.message = message;
    }

    private String message;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
