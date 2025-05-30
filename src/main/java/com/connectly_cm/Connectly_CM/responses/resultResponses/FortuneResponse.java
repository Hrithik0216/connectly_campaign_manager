package com.connectly_cm.Connectly_CM.responses.resultResponses;


import com.fasterxml.jackson.annotation.JsonInclude;


public class FortuneResponse {
    private int statusCode;
    private Object data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    private FortuneResponse(Builder builderData) {
        this.statusCode = builderData.statusCode;
        this.data = builderData.data;
        this.message = builderData.message;
    }

    public static class Builder {
        private int statusCode;
        private Object data;
        private String message;

        public Builder(int statusCode, Object data) {
            this.statusCode = statusCode;
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public FortuneResponse setMessage(String message) {
            this.message = message;
            return new FortuneResponse(this);
        }
        public FortuneResponse build(){
            return new FortuneResponse(this);
        }
    }
}
