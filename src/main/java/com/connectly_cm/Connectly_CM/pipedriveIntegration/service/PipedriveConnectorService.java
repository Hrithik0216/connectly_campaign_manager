package com.connectly_cm.Connectly_CM.pipedriveIntegration.service;

import com.connectly_cm.Connectly_CM.Utils.DateUtils.DateTimeUtils;
import com.connectly_cm.Connectly_CM.Utils.EncryptionAes.EncryptionAes;
import com.connectly_cm.Connectly_CM.Utils.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.Utils.usersUtils.model.User;
import com.connectly_cm.Connectly_CM.constants.CrmConstants;
import com.connectly_cm.Connectly_CM.Utils.usersUtils.repository.UserRepository;
import com.connectly_cm.Connectly_CM.pipedriveIntegration.Model.CrmSettings;
import com.connectly_cm.Connectly_CM.pipedriveIntegration.Repository.CrmSettingRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;


@Service
public class PipedriveConnectorService {
    private static final Logger LOGGER = Logger.getLogger(PipedriveConnectorService.class);

    @Autowired
    UserRepository userRepository;

    @Value("${pipedrive.client.id}")
    private String clientId;

    @Value("${pipedrive.client.secret}")
    private String clientSecret;

    @Value("${pipedrive.api.token}")
    private String pipedriveApikey;

    @Autowired
    CrmSettingRepository crmSettingRepository;


    public ResponseEntity<?> authenticate(String userId) {
        if (userRepository.existsById(userId)) {
            LOGGER.info("The user exists. UserID: " + userId);
            String authenticationUrl = CrmConstants.PIPEDRIVE_OAUTH_URL + "client_id=" + clientId + "&redirect_uri=" + CrmConstants.PIPEDRIVE_REDIRECT_URL;
            LOGGER.info("The pipedrive authentication url is " + authenticationUrl);
            HashMap<String, String> resp = new HashMap<>();
            resp.put("authorizationUrl", authenticationUrl);
            return ResponseEntity.status(HttpStatus.OK).body(resp);
        } else {
            LOGGER.info("The userId is not found in db " + userId + ".");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The user with userId " + userId + " not found in DB");
        }

    }

    public JSONObject getTokens(String authCode, String userId) {
        LOGGER.info("Getting tokens for the auth code: " + authCode);

        if (!userRepository.existsById(userId)) {
            return new JSONObject().put("Error", "The user does not exist in DB");
        }
        if (userRepository.existsById(userId)) {
            if (!crmSettingRepository.checkByUserId(userId)) {
                Optional<User> userData = userRepository.findById(userId);
                String getTokenUrl = CrmConstants.PIPEDRIVE_GET_TOKENS_URL;
                String basicAuthCred = clientId + ":" + clientSecret;
                String token64 = new String(Base64.getEncoder()
                        .encode(basicAuthCred.getBytes()));
                String auth = CrmConstants.AUTH_TYPE + token64;

                HttpHeaders headers = new HttpHeaders();
                headers.set(CrmConstants.AUTHORIZATION, auth);
                headers.setContentType(CrmConstants.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("grant_type", CrmConstants.PIPEDRIVE_GRANT_TYPE);
                map.add("code", authCode);
                map.add("redirect_uri", CrmConstants.PIPEDRIVE_REDIRECT_URL);


                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

                RestTemplate restTemplate = new RestTemplate();
                try {
                    ResponseEntity<String> response = restTemplate.postForEntity(getTokenUrl, request, String.class);
                    LOGGER.info("The response status is " + response.getStatusCode());
                    LOGGER.info("The response body is " + response.getBody());
                    JSONObject jsonRes = new JSONObject(response.getBody());
                    CrmSettings crmSettings = new CrmSettings();
                    crmSettings.setAccessToken(EncryptionAes.localEncrypt(jsonRes.getString("access_token")));
                    crmSettings.setRefreshToken(EncryptionAes.localEncrypt(jsonRes.getString("refresh_token")));
                    crmSettings.setType(CrmConstants.PIPEDRIVE_CRM);
                    crmSettings.setCreateTs(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), null));
                    crmSettings.setUpdateTs(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), null));
                    crmSettings.setScopes(StringUtil.listSeparatedByComma(jsonRes.getString("scope")));
                    crmSettings.setApiDomain(jsonRes.getString("api_domain"));
                    crmSettings.setTokenType(jsonRes.getString("token_type"));
                    crmSettings.setAccessTokenExpiryDate(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), jsonRes.getInt("expires_in")));
                    if (userData.isPresent()) {
                        crmSettings.setUserId(userData.get().getId());
                        crmSettings.setConnectedEmail(userData.get().getEmail());
                    }
                    LOGGER.info("Saving the crm settings for the user with userId " + userId);
                    crmSettingRepository.save(crmSettings);
                    return jsonRes;
                } catch (HttpClientErrorException | HttpServerErrorException ex) {
                    LOGGER.warn("Error caused due to " + ex.getMessage());
                    return new JSONObject().put("error", ex.getMessage());
                } catch (Exception e) {
                    LOGGER.warn("Internal server error" + e.getMessage());
                    return new JSONObject().put("error", "Internal server err: " + e.getMessage());
                }
            } else {
                LOGGER.info("An account is already connected. Please reconnect or disconnect it");
            }

        } else {
            return new JSONObject().put("Error", "UserId Does not exist");
        }
        return new JSONObject().put("Error", "An account is already connected. Please reconnect or disconnect it");
    }

    public ResponseEntity<?> getContacts(String userId) {
        if (userRepository.existsById(userId)) {
            LOGGER.info("User ID exist "+userId);
            if(crmSettingRepository.checkByUserId(userId)){
                LOGGER.info("UserID's crm setting data exist");
                Optional<List<CrmSettings>> userCrmData = crmSettingRepository.findByUserId(userId);
                for (CrmSettings crmSetting : userCrmData.get()) {
                    LOGGER.info("Crm setting "+crmSetting);
                    try {
                        Date expiryDate = DateTimeUtils
                                .convertDateStringTODate(crmSetting.getAccessTokenExpiryDate());
                        Date currDate = new Date();
                        if (expiryDate.compareTo(currDate) > 0 || expiryDate.compareTo(currDate) == 0) {
                            LOGGER.info("Access token has not expired. Using the same");
                            String accessToken = EncryptionAes.localDecrypt(crmSetting.getAccessToken());
                            String url = CrmConstants.PIPEDRIVE_COMPANY_DOMAIN + CrmConstants.GET_ALL_CONTACTS;
                            Map<String, Object> result =makeApiCall(accessToken, url);
                            return ResponseEntity.status(HttpStatus.OK).body(result);
                        } else {
                            LOGGER.info("Access token has expired. Using refresh token to get access token for the user " + userId);
                            String updatedAcessToken = accessTokenUsingRefreshToken(userId, EncryptionAes.localDecrypt(crmSetting.getRefreshToken()));
                            String url = CrmConstants.PIPEDRIVE_COMPANY_DOMAIN + CrmConstants.GET_ALL_CONTACTS;
                            Map<String, Object> result=makeApiCall(updatedAcessToken, url);
                            return ResponseEntity.status(HttpStatus.OK).body(result);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }
            }



        }
        return ResponseEntity.status(HttpStatus.OK).body("Failed");
    }


    private String accessTokenUsingRefreshToken(String userId, String refreshToken) {
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(refreshToken)) {
            LOGGER.info("User Id or refresh token is empty");
            throw new IllegalArgumentException("User ID and refresh token must not be empty");
        }

        if (!crmSettingRepository.checkByUserId(userId)) {
            LOGGER.info("User does not exists");
            throw new RuntimeException("user does not exist");
        }

        try {
            String url = CrmConstants.PIPEDRIVE_GET_TOKENS_URL;
            String authCredentials = clientId + ":" + clientSecret;
            String token64 = Base64.getEncoder().encodeToString(authCredentials.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(token64);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", CrmConstants.PIPEDRIVE_REFRESH_TOKEN_GRANT_TYPE);
            body.add("refresh_token", refreshToken);
            body.add("redirect_uri", CrmConstants.PIPEDRIVE_REDIRECT_URL);

            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(body,headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(url,request,String.class);
            if(!response.getStatusCode().is2xxSuccessful()){
                throw new RuntimeException("Failed to fetch the refresh token");
            }

            JSONObject resJson = new JSONObject(response.getBody());
            return resJson.getString("access_token");

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Client error during token refresh: " + e.getResponseBodyAsString(), e);
        }
    }

    private Map<String, Object> makeApiCall(String accessToken, String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string(); // Read once
            LOGGER.info("Status code: " + response.code());
            LOGGER.info("Response body: " + responseBody);
            return new JSONObject(responseBody).toMap(); // Now safe to parse
        } catch (IOException e) {
            LOGGER.warn("Exception during API call: " + e.getMessage(), e);
        }
        return Collections.singletonMap("Error", "Error while fetching data");
    }

}
