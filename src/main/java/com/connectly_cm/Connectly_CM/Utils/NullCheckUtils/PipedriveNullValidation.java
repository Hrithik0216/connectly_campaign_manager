package com.connectly_cm.Connectly_CM.Utils.NullCheckUtils;

import org.apache.juli.logging.Log;
import org.apache.log4j.Logger;

public class PipedriveNullValidation {
    private static final Logger LOGGER = Logger.getLogger(PipedriveNullValidation.class);
    public static void validateUserId(String userId){
        if(userId==null){
            LOGGER.warn("The userId is null");
            throw new IllegalArgumentException("The userId is null");
        }
    }

    public static void validateAuthCode(String authCode){
        if(authCode==null){
            LOGGER.warn("The authorization code is null");
            throw new IllegalArgumentException("The authorization code is null");
        }
    }

    public static void validateRefreshToken(String refreshToken){
        if(refreshToken==null){
            LOGGER.warn("The authorization code is null");
            throw new IllegalArgumentException("The authorization code is null");
        }
    }

}
