package com.connectly_cm.Connectly_CM.responses.resultResponses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

public class ErrResponse {
    private String message;
    private int statusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date timeStamp;

    private ErrResponse(Builder data) {
        this.message = data.message;
        this.statusCode=data.statusCode;
        this.timeStamp = data.timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public static class Builder {
        private String message;
        private int statusCode;
        private Date timeStamp;

        public Builder(String message, int statusCode) {
            this.message = message;
            this.statusCode = statusCode;
        }

        public Date getTimeStamp() {
            return timeStamp;
        }

        public Builder setTimeStamp(Date timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public ErrResponse build() {
            return new ErrResponse(this);
        }

    }
}
