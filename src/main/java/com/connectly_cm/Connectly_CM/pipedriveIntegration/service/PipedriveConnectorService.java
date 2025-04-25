package com.connectly_cm.Connectly_CM.pipedriveIntegration.service;

import com.connectly_cm.Connectly_CM.CustomErrorCodes.CrmErrCode;
import com.connectly_cm.Connectly_CM.CustomErrorCodes.MemberErrCode;
import com.connectly_cm.Connectly_CM.Utils.DateUtils.DateTimeUtils;
import com.connectly_cm.Connectly_CM.Utils.EncryptionAes.EncryptionAes;
import com.connectly_cm.Connectly_CM.Utils.HttpClientUtils.PipedriveHttpClientUtils.CrmHttpUtils;
import com.connectly_cm.Connectly_CM.Utils.NullCheckUtils.PipedriveNullValidation;
import com.connectly_cm.Connectly_CM.Utils.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.Utils.UrlBuilder.UrlBuilder;
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
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", "The userId does not exist"));
        }
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
        PipedriveNullValidation.validateAuthCode(authCode);
        PipedriveNullValidation.validateUserId(userId);

        LOGGER.info("Getting tokens for the auth code: " + authCode);
        if (!userRepository.existsById(userId)) {
            return new JSONObject().put(MemberErrCode.MEMBER_DOES_NO_EXIST, "The user does not exist in DB");
        }
        if (userRepository.existsById(userId)) {
            if (!crmSettingRepository.checkByUserId(userId)) {
                Optional<User> userData = userRepository.findById(userId);
                JSONObject jsonRes = CrmHttpUtils.basicAuthorization(CrmConstants.PIPEDRIVE_CRM, authCode);
                LOGGER.info("Raw response from basicAuthorization: " + jsonRes.toString());
                CrmSettings crmSettings = new CrmSettings();
                try {
                    crmSettings.setAccessToken(EncryptionAes.localEncrypt(jsonRes.getString("access_token")));
                    crmSettings.setRefreshToken(EncryptionAes.localEncrypt(jsonRes.getString("refresh_token")));
                } catch (Exception e) {
                    LOGGER.warn("Error occurred while encrypting tokens: " + e.getMessage());
                    throw new RuntimeException();
                }

                crmSettings.setType(CrmConstants.PIPEDRIVE_CRM);
                crmSettings.setCreateTs(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), null));
                crmSettings.setUpdateTs(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), null));
                crmSettings.setScopes(StringUtil.listSeparatedByComma(jsonRes.getString("scope")));
                crmSettings.setApiDomain(jsonRes.getString("api_domain"));
                crmSettings.setTokenType(jsonRes.getString("token_type"));
                crmSettings.setAccessTokenExpiryDate(DateTimeUtils.convertDateToString(new Date(),
                        TimeZone.getTimeZone("UTC"),
                        jsonRes.getInt("expires_in")));
                if (userData.isPresent()) {
                    crmSettings.setUserId(userData.get().getId());
                    crmSettings.setConnectedEmail(userData.get().getEmail());
                }
                LOGGER.info("Saving the crm settings for the user with userId " + userId);
                crmSettingRepository.save(crmSettings);
                return jsonRes;
            } else {
                LOGGER.info("An account is already connected. Please reconnect or disconnect it");
            }

        } else {
            return new JSONObject().put("Error", "UserId Does not exist");
        }
        return new JSONObject()
                .put(CrmErrCode.ACCOUNT_ALREADY_CONNECTED, "An account is already connected. Please reconnect or disconnect it");
    }

    private JSONObject getAccessTokenUsingRefreshToken(String userId, String refreshToken) {
        PipedriveNullValidation.validateUserId(userId);
        PipedriveNullValidation.validateRefreshToken(refreshToken);
        if (!crmSettingRepository.checkByUserId(userId)) {
            LOGGER.info("User's crm setting does not exists");
            return new JSONObject().put("Error", "user's crm setting does not exists");
        }
        JSONObject response = CrmHttpUtils.basicAuthorization(CrmConstants.PIPEDRIVE_REFRESH_TOKEN, refreshToken);
        return response;
    }

    private Map<String, Object> makeApiCall(String accessToken, String url) {
        if (StringUtil.isEmpty(accessToken)) {
            throw new IllegalArgumentException("The access token is empty");
        }
        if (StringUtil.isEmpty(url)) {
            throw new IllegalArgumentException("The URL token is empty");
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            LOGGER.info("Status code: " + response.code());
            LOGGER.info("Response body: " + responseBody);
            return new JSONObject(responseBody).toMap();
        } catch (IOException e) {
            LOGGER.warn("Exception during API call: " + e.getMessage(), e);
        }
        return Map.of("Error", "Error while fetching data");
    }

    public ResponseEntity<?> getContacts(String userId) {
        PipedriveNullValidation.validateUserId(userId);

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", "User Does not exists"));
        }
        LOGGER.info("User ID exist " + userId);

        if (!crmSettingRepository.checkByUserId(userId)) {
            LOGGER.info("User's crm setting does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", "User's crm doest not exist"));
        }

        LOGGER.info("UserID's crm setting data exist");
        CrmSettings userCrmSetting = crmSettingRepository.findByUserId(userId);
        LOGGER.info("Crm setting " + userCrmSetting);
        try {
            Date expiryDate = DateTimeUtils.convertDateStringTODate(userCrmSetting.getAccessTokenExpiryDate());
            if (!expiryDate.before(new Date())) {
                LOGGER.info("Access token has not expired. Using the same");
                String accessToken = EncryptionAes.localDecrypt(userCrmSetting.getAccessToken());
                String url = CrmConstants.PIPEDRIVE_COMPANY_DOMAIN + CrmConstants.GET_ALL_CONTACTS;
                Map<String, Object> result = makeApiCall(accessToken, url);
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } else {
                LOGGER.info("Access token has expired. Using refresh token to get access token for the user " + userId);
                JSONObject resJson = getAccessTokenUsingRefreshToken(userId, EncryptionAes.localDecrypt(userCrmSetting.getRefreshToken()));
                if (resJson.has("error")) {
                    LOGGER.warn("Error fetching tokens using refresh tokens");
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("Error", "Error fetching tokens"));
                }
                if (!resJson.has("access_token")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", "access token is not found"));
                }
//                if(resJson.has("refresh_token")){
//                    userCrmSetting.setRefreshToken(EncryptionAes.localEncrypt(resJson.getString("refresh_token")));
//                }

//                userCrmSetting.setAccessToken(EncryptionAes.localEncrypt(updatedAcessToken));
//                userCrmSetting.setUpdateTs(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), null));
//                userCrmSetting.setAccessTokenExpiryDate(DateTimeUtils.convertDateToString(new Date(),
//                        TimeZone.getTimeZone("UTC"),
//                        resJson.getInt("expires_in")));
                String updatedAcessToken = resJson.getString("access_token");
                CrmHttpUtils.updateCrmSetting(userCrmSetting,resJson);
                crmSettingRepository.save(userCrmSetting);
                String url = CrmConstants.PIPEDRIVE_COMPANY_DOMAIN + CrmConstants.GET_ALL_CONTACTS;
                Map<String, Object> result = makeApiCall(updatedAcessToken, url);
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
        } catch (Exception e) {
            LOGGER.info("Error occured due to " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> getLeadContacts(String userId, Map<String, Object> requestBody) {
        PipedriveNullValidation.validateUserId(userId);
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", "UserId does not exist"));
        }
        LOGGER.info("UserId exists");
        if (!crmSettingRepository.checkByUserId(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", "user's crm setting is not found"));
        }
        LOGGER.info("User's crm setting exists");
        CrmSettings userCrmSetting = crmSettingRepository.findByUserId(userId);
        String builtUrl = UrlBuilder.urlBuilderWithParam(CrmConstants.PIPEDRIVE_BASE_URL + CrmConstants.PIPEDRIVE_LEADS, requestBody);

        if (userCrmSetting == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", "User's Crm setting does not exist"));
        }

        LOGGER.info("user has crm Setting");
        Date expiryDate = DateTimeUtils.convertDateStringTODate(userCrmSetting.getAccessTokenExpiryDate());
        if (!expiryDate.before(new Date())) {
            LOGGER.info("Token has a valid expiry date");

            LOGGER.info("Built Url: " + builtUrl);
            try {
                String accessToken = EncryptionAes.localDecrypt(userCrmSetting.getAccessToken());
                Map<String, Object> result = makeApiCall(accessToken, builtUrl);
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } catch (Exception e) {
                LOGGER.warn("Exception is " + e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.info("Access token has expired. Using refresh token to get access token for the user " + userId);
            try {
                JSONObject newCredential = getAccessTokenUsingRefreshToken(userId, EncryptionAes.localDecrypt(userCrmSetting.getRefreshToken()));

                if (newCredential.has("error")) {
                    LOGGER.warn("Error fetching tokens using refresh tokens");
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("Error", "Error fetching tokens"));
                }

                LOGGER.info("New credentials: " + newCredential.toString());
                if (!newCredential.has("access_token")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", "access token is not found"));
                }
//                if(newCredential.has("refresh_token")){
//                    userCrmSetting.setRefreshToken(EncryptionAes.localEncrypt(newCredential.getString("refresh_token")));
//                }

//                userCrmSetting.setAccessToken(EncryptionAes.localEncrypt(newAccessToken));
//                userCrmSetting.setAccessTokenExpiryDate(DateTimeUtils.convertDateToString(new Date(),
//                        TimeZone.getTimeZone("UTC"),
//                        newCredential.getInt("expires_in")));
//                userCrmSetting.setUpdateTs(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), null));
                String newAccessToken = newCredential.getString("access_token");
                CrmHttpUtils.updateCrmSetting(userCrmSetting,newCredential);
                crmSettingRepository.save(userCrmSetting);

                Map<String, Object> result = makeApiCall(newAccessToken, builtUrl);
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } catch (Exception e) {
                LOGGER.info("The err is " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
