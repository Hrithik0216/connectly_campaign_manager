package com.connectly_cm.Connectly_CM.ErrResponses;

import org.springframework.http.HttpStatus;

public class UserDataErrResponse {
    private HttpStatus errCode;
    private String errMessage;
    public UserDataErrResponse(HttpStatus errCode, String errMessage){
        this.errCode=errCode;
        this.errMessage=errMessage;
    }
}
