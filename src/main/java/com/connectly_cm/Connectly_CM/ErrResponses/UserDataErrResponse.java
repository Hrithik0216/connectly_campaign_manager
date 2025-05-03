package com.connectly_cm.Connectly_CM.ErrResponses;

public class UserDataErrResponse {
    private String errCode;
    private String errMessage;
    public UserDataErrResponse(String errCode, String errMessage){
        this.errCode=errCode;
        this.errMessage=errMessage;
    }
}
