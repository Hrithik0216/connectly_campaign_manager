package com.connectly_cm.Connectly_CM.Utils.NullCheckUtils;

public class PipedriveNullValidation {
    public static void validateUserId(String userId){
        if(userId==null){
            throw new IllegalArgumentException("The userId is null");
        }
    }

    public static void validateAuthCode(String authCode){
        if(authCode==null){
            throw new IllegalArgumentException("The authorization code is null");
        }
    }

}
