package com.connectly_cm.Connectly_CM.constants;

import org.springframework.http.MediaType;

public final class CrmConstants {

    public static final String PIPEDRIVE_OAUTH_URL = "https://oauth.pipedrive.com/oauth/authorize?";
    public static final String PIPEDRIVE_REDIRECT_URL = "http://localhost:3000/connect/pipedrive/callback";
    public static final String PIPEDRIVE_GET_TOKENS_URL = "https://oauth.pipedrive.com/oauth/token";
    public static final String AUTH_TYPE = "Basic ";
    public static final String AUTHORIZATION = "Authorization";
    public static final String PIPEDRIVE_GRANT_TYPE = "authorization_code";
    public static final String PIPEDRIVE_REFRESH_TOKEN_GRANT_TYPE = "refresh_token";
    public static final String PIPEDRIVE_APIKEY_AUTHORIZATION = "x-api-key";
    public static final MediaType APPLICATION_FORM_URLENCODED = new MediaType("application", "x-www-form-urlencoded");
    public static final String PIPEDRIVE_BASE_URL = "https://api.pipedrive.com/v1";
    public static final String PIPEDRIVE_COMPANY_DOMAIN = "https://hrithik-sandbox.pipedrive.com";
    public static final String PIPEDRIVE_PERSONS = "/persons";
    public static final String GET_ALL_CONTACTS = "/v1/persons";
    public static final String PIPEDRIVE_CRM ="PIPEDRIVE";

    public CrmConstants() {
    }
}
