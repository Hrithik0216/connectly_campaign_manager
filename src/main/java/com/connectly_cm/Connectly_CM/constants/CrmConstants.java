package com.connectly_cm.Connectly_CM.constants;

import org.springframework.http.MediaType;

public final class CrmConstants {

    public static final String PIPEDRIVE_OAUTH_URL = "https://oauth.pipedrive.com/oauth/authorize?";
    public static final String PIPEDRIVE_REDIRECT_URL = "http://localhost:3000/connect/pipedrive/callback";
    public static final String PIPEDRIVE_GET_TOKENS_URL = "https://oauth.pipedrive.com/oauth/token";
    public static final String AUTH_TYPE = "Basic ";
    public static final String AUTHORIZATION = "Authorization";
    public static final String PIPEDRIVE_GRANT_TYPE  ="authorization_code";
    public static final MediaType APPLICATION_FORM_URLENCODED = new MediaType("application", "x-www-form-urlencoded");

    public CrmConstants() {
    }
}
