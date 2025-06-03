package com.connectly_cm.Connectly_CM.utils.HttpClientUtils.PipedriveHttpClientUtils;

import com.connectly_cm.Connectly_CM.constants.GoogleConstants;
import com.connectly_cm.Connectly_CM.models.connectInboxModels.ConnectedGmailAccount;
import com.connectly_cm.Connectly_CM.models.connectInboxModels.UnifiedInboxAccounts;
import com.connectly_cm.Connectly_CM.utils.DateUtils.DateTimeUtils;
import com.connectly_cm.Connectly_CM.utils.EncryptionAes.EncryptionAes;
import com.connectly_cm.Connectly_CM.constants.CrmConstants;
import com.connectly_cm.Connectly_CM.models.pipedrive.CrmSettings;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class CrmHttpUtils {

    public static final Logger LOGGER = Logger.getLogger(CrmHttpUtils.class);

    private static String pipedriveClientId = "";
    private static String pipedriveClientSecret = "";
    private static String gmailClientId = "";
    private static String gmailClientSecret = "";

    @Value("${pipedrive.client.id}")
    private String tempPipedriveClientId;
    @Value("${pipedrive.client.secret}")
    private String tempPipedriveClientSecret;

    @Value("${google.client-id}")
    private String tempGmailClientId;

    @Value("${google.client-secret}")
    private String tempGmailClientSecret;


    @PostConstruct
    public void init() {
        if (tempPipedriveClientId == null || tempPipedriveClientSecret == null) {
            LOGGER.info("tempPipedriveClientId/tempPipedriveClientSecret is null");
            throw new IllegalStateException("We have missing pipedrive credentials");
        }
        if (tempGmailClientId == null || tempGmailClientSecret == null) {
            LOGGER.info("tempGmailClientId/tempGmailClientSecret is null");
            throw new IllegalStateException("We have missing gmail credentials");
        }
        pipedriveClientId = tempPipedriveClientId;
        pipedriveClientSecret = tempPipedriveClientSecret;
        LOGGER.info("pipedriveClientId: " + pipedriveClientId);
        LOGGER.info("pipedriveClientSecret" + pipedriveClientSecret);
        gmailClientId = tempGmailClientId;
        gmailClientSecret = tempGmailClientSecret;
        LOGGER.info("gmailClientId: " + gmailClientId);
        LOGGER.info("gmailClientSecret" + gmailClientSecret);
    }

    public static JSONObject basicAuthorization(String type, String authCode) {
        String secretKey = pipedriveClientId + ":" + pipedriveClientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(secretKey.getBytes());
        HttpHeaders headers;
        switch (type) {
            case "PIPEDRIVE":
                LOGGER.info("Case: Pipedrive Basic Auth");
                LOGGER.info("Using clientId: " + pipedriveClientId);
                LOGGER.info("Using authCode: " + authCode);
                LOGGER.info("Encoded auth: " + encodedAuth);
                headers = getAuthHeaders(type, encodedAuth);
                MultiValueMap<String, String> map = getMultivalueMap(CrmConstants.PIPEDRIVE_CRM, authCode);
                return makeHttpRequest(map, headers, HttpMethod.POST, type);
            case "PIPEDRIVE_REFRESH_TOKEN":
                LOGGER.info("Case: Pipedrive accessing refreshtoken");
                LOGGER.info("Using clientId: " + pipedriveClientId);
                LOGGER.info("Using refresh Token: " + authCode);
                LOGGER.info("Encoded auth: " + encodedAuth);
                headers = getAuthHeaders(CrmConstants.PIPEDRIVE_REFRESH_TOKEN, encodedAuth);
                MultiValueMap<String, String> refreshTokenMap = getMultivalueMap(CrmConstants.PIPEDRIVE_REFRESH_TOKEN, authCode);
                return makeHttpRequest(refreshTokenMap, headers, HttpMethod.POST, type);
            case "GMAIL_REFRESH_TOKEN":
                LOGGER.info("Case: Gmail api's refreshtoken");
                LOGGER.info("Using clientId: " + gmailClientId);
                headers = getAuthHeaders(CrmConstants.PIPEDRIVE_REFRESH_TOKEN, encodedAuth);
                MultiValueMap<String, String> gmailRefreshTokenMap = getMultivalueMap(GoogleConstants.GMAIL_REFRESH_TOKEN, authCode);
                return makeHttpRequest(gmailRefreshTokenMap, headers, HttpMethod.POST, type);
            default:
                return new JSONObject();
        }

    }

    public static HttpHeaders getAuthHeaders(String type, String encodedAuth) {
        HttpHeaders headers = new HttpHeaders();
        switch (type) {
            case "PIPEDRIVE":
                headers.set(CrmConstants.AUTHORIZATION, CrmConstants.AUTH_TYPE + encodedAuth);
                headers.setContentType(CrmConstants.APPLICATION_FORM_URLENCODED);
                return headers;
            case "PIPEDRIVE_REFRESH_TOKEN":
                headers.setBasicAuth(encodedAuth);
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                return headers;
            default:
                return headers;
        }
    }

    public static MultiValueMap<String, String> getMultivalueMap(String type, String authCode) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        switch (type) {
            case "PIPEDRIVE":
                map.add("grant_type", CrmConstants.PIPEDRIVE_GRANT_TYPE);
                map.add("code", authCode);
                map.add("redirect_uri", CrmConstants.PIPEDRIVE_REDIRECT_URL);
                return map;

            case "PIPEDRIVE_REFRESH_TOKEN":
                map.add("grant_type", CrmConstants.PIPEDRIVE_REFRESH_TOKEN_GRANT_TYPE);
                map.add("refresh_token", authCode);
                map.add("redirect_uri", CrmConstants.PIPEDRIVE_REDIRECT_URL);
                return map;

            case "GMAIL_REFRESH_TOKEN":
                map.add("client_id", gmailClientId);
                map.add("client_secret", gmailClientSecret);
                map.add("refresh_token", authCode);
                map.add("grant_type", GoogleConstants.GMAIL_REFRESH_TOKEN_GRANT_TYPE);
                return map;

            default:
                return map;
        }
    }

    public static JSONObject makeHttpRequest(MultiValueMap<String, String> map, HttpHeaders headers, HttpMethod requestMethod, String type) {
        LOGGER.info("MultiValueMapMap for request " + map);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        RestTemplate restTemplate = new RestTemplate();
        switch (type) {
            case "PIPEDRIVE":
                try {
                    String url = CrmConstants.PIPEDRIVE_GET_TOKENS_URL;
                    ResponseEntity<String> response = restTemplate.exchange(url, requestMethod, request, String.class);
                    LOGGER.info("Response status: " + response.getStatusCode());
                    LOGGER.info("Response body: " + response.getBody());
                    try {
                        JSONObject jsonResponse = new JSONObject(response.getBody());
                        LOGGER.info("Parsed JSON response: " + jsonResponse.toString());
                        return jsonResponse;
                    } catch (JSONException e) {
                        LOGGER.error("Failed to parse JSON response: " + e.getMessage());
                        return new JSONObject().put("error", "Failed to parse response: " + e.getMessage());
                    }

                } catch (HttpClientErrorException e) {
                    LOGGER.warn("HttpClientErrorException Error occurred while fetching tokens: " + e.getMessage());
                    return new JSONObject().put("error", e.getMessage());
                } catch (HttpServerErrorException e) {
                    LOGGER.warn("HttpServerErrorException Error occurred while fetching tokens:" + e.getMessage());
                    return new JSONObject().put("error", e.getMessage());
                } catch (Exception e) {
                    LOGGER.warn("Error occurred while fetching tokens: " + e.getMessage());
                    return new JSONObject().put("error", e.getMessage());
                }

            case "PIPEDRIVE_REFRESH_TOKEN":
                try {
                    String url = CrmConstants.PIPEDRIVE_GET_TOKENS_URL;
                    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                    LOGGER.info("Response status: " + response.getStatusCode());
                    LOGGER.info("Response body: " + response.getBody());
                    try {
                        JSONObject jsonResponse = new JSONObject(response.getBody());
                        LOGGER.info("Parsed JSON response refresh token: " + jsonResponse.toString());
                        return jsonResponse;
                    } catch (JSONException e) {
                        LOGGER.error("Failed to parse JSON response: " + e.getMessage());
                        return new JSONObject().put("error", "Failed to parse response: " + e.getMessage());
                    }
                } catch (HttpClientErrorException e) {
                    LOGGER.warn("HttpClientErrorException Error occurred while fetching tokens: " + e.getMessage());
                    return new JSONObject().put("error", e.getMessage());
                } catch (HttpServerErrorException e) {
                    LOGGER.warn("HttpServerErrorException Error occurred while fetching tokens:" + e.getMessage());
                    return new JSONObject().put("error", e.getMessage());
                } catch (Exception e) {
                    LOGGER.warn("Error occured while fetching acccess token usn");
                    throw new RuntimeException(e);
                }

            case "GMAIL_REFRESH_TOKEN":
                try {
                    String url = GoogleConstants.GMAIL_BASE_URL;
                    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                    LOGGER.info("Response status: " + response.getStatusCode());
                    LOGGER.info("Response body: " + response.getBody());
                    try {
                        JSONObject jsonResponse = new JSONObject(response.getBody());
                        LOGGER.info("Parsed JSON response refresh token: " + jsonResponse.toString());
                        return jsonResponse;
                    } catch (JSONException e) {
                        LOGGER.error("Failed to parse JSON response: " + e.getMessage());
                        return new JSONObject().put("error", "Failed to parse response: " + e.getMessage());
                    }
                } catch (HttpClientErrorException e) {
                    LOGGER.warn("HttpClientErrorException Error occurred while fetching tokens: " + e.getMessage());
                    return new JSONObject().put("error", e.getMessage());
                } catch (HttpServerErrorException e) {
                    LOGGER.warn("HttpServerErrorException Error occurred while fetching tokens:" + e.getMessage());
                    return new JSONObject().put("error", e.getMessage());
                } catch (Exception e) {
                    LOGGER.warn("Error occured while fetching acccess token usn");
                    throw new RuntimeException(e);
                }
            default:
                return new JSONObject();
        }
    }

    public static void updateCrmSetting(CrmSettings crmSettings, JSONObject response) {
        try {
            LOGGER.info("Updating user setting after fetching new credentials");
            if (response.has("refresh_token")) {
                crmSettings.setRefreshToken(EncryptionAes.localEncrypt(response.getString("refresh_token")));
                crmSettings.setAccessToken(EncryptionAes.localEncrypt(response.getString("access_token")));
                crmSettings.setAccessTokenExpiryDate(DateTimeUtils.convertDateToString(new Date(),
                        TimeZone.getTimeZone("UTC"),
                        response.getInt("expires_in")));
                crmSettings.setUpdateTs(DateTimeUtils.convertDateToString(new Date(),
                        TimeZone.getTimeZone("UTC"), null));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateUnifiedAccData(UnifiedInboxAccounts connectedMailAccs,
                                            String fromAddress, JSONObject response){
        try {
            LOGGER.info("Updating Unified account setting after fetching new credentials");
            if (response.has("access_token")) {
                List<ConnectedGmailAccount> connectedMails = connectedMailAccs.getConnectedEmailAccounts();
                Optional<ConnectedGmailAccount> connectedAcc = connectedMails.stream()
                        .filter(connectedMail -> connectedMail.getConnectedMail().equals(fromAddress))
                        .findFirst();
                ConnectedGmailAccount acc = connectedAcc.get();
                acc.setAccessToken(response.getString("access_token"));
                acc.setTokenExpiryTime(new Date(System.currentTimeMillis() +
                        response.getLong("expires_in") * 1000));
                acc.setScopes(Collections.singletonList(response.getString("scope")));
                acc.setRefreshTokenExpiry(new Date(System.currentTimeMillis() +
                        response.getLong("refresh_token_expires_in") * 1000));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
