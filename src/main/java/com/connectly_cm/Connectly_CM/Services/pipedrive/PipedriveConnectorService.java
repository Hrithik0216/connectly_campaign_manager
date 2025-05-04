package com.connectly_cm.Connectly_CM.Services.pipedrive;

import com.connectly_cm.Connectly_CM.CustomErrorCodes.CrmErrCode;
import com.connectly_cm.Connectly_CM.CustomErrorCodes.MemberErrCode;
import com.connectly_cm.Connectly_CM.dtos.pipedrive.CrmOwnerDetails;
import com.connectly_cm.Connectly_CM.dtos.pipedrive.EmailData;
import com.connectly_cm.Connectly_CM.dtos.pipedrive.PhoneData;
import com.connectly_cm.Connectly_CM.models.pipedrive.CrmContacts;
import com.connectly_cm.Connectly_CM.repositories.pipedrive.CrmContactRepository;
import com.connectly_cm.Connectly_CM.utils.DateUtils.DateTimeUtils;
import com.connectly_cm.Connectly_CM.utils.EncryptionAes.EncryptionAes;
import com.connectly_cm.Connectly_CM.utils.HttpClientUtils.PipedriveHttpClientUtils.CrmHttpUtils;
import com.connectly_cm.Connectly_CM.utils.NullCheckUtils.PipedriveNullValidation;
import com.connectly_cm.Connectly_CM.utils.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.utils.UrlBuilder.UrlBuilder;
import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.constants.CrmConstants;
import com.connectly_cm.Connectly_CM.repositories.userRepository.UserRepository;
import com.connectly_cm.Connectly_CM.models.pipedrive.CrmSettings;
import com.connectly_cm.Connectly_CM.repositories.pipedrive.CrmSettingRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


@Service
public class PipedriveConnectorService {
    private static final Logger LOGGER = Logger.getLogger(PipedriveConnectorService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    CrmContactRepository crmContactRepository;

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

        if (!crmSettingRepository.checkByUserId(userId)) {
            Optional<User> userData = userRepository.findById(userId);
            JSONObject jsonRes = CrmHttpUtils.basicAuthorization(CrmConstants.PIPEDRIVE_CRM, authCode);
            if (jsonRes.has("error")) {
                return new JSONObject().put("error", "The oauth code has expired");
            }
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
            return new JSONObject()
                    .put(String.valueOf(HttpStatus.OK), "An account is already connected. Please reconnect or disconnect it");
        }
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
        LOGGER.info("User ID exist " + userId);
        if (!crmSettingRepository.checkByUserId(userId)) {
            LOGGER.info("User's crm setting does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", "User's crm doest not exist"));
        }

        LOGGER.info("UserID's crm setting data exist");
        CrmSettings userCrmSetting = crmSettingRepository.findByUserId(userId);
        LOGGER.info("Crm setting " + userCrmSetting.toString());
        try {
            Date expiryDate = DateTimeUtils.convertDateStringTODate(userCrmSetting.getAccessTokenExpiryDate());
            if (!expiryDate.before(new Date())) {
                LOGGER.info("Access token has not expired. Using the same");
                String accessToken = EncryptionAes.localDecrypt(userCrmSetting.getAccessToken());
                String url = CrmConstants.PIPEDRIVE_COMPANY_DOMAIN + CrmConstants.GET_ALL_CONTACTS;
                Map<String, Object> result = makeApiCall(accessToken, url);
                ContactsResponse(result, userId, CrmConstants.PIPEDRIVE_PERSON);
                return ResponseEntity.status(HttpStatus.OK).body(result);
            } else {
                LOGGER.info("Access token has expired. Using refresh token to get access token for the user " + userId);
                JSONObject resJson = getAccessTokenUsingRefreshToken(userId, EncryptionAes.localDecrypt(userCrmSetting.getRefreshToken()));
                if (resJson.has("error")) {
                    LOGGER.warn("Error fetching tokens using refresh tokens");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("Error", "Error fetching tokens"));
                }
                if (!resJson.has("access_token")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", "access token is not found"));
                }
                String updatedAcessToken = resJson.getString("access_token");
                CrmHttpUtils.updateCrmSetting(userCrmSetting, resJson);
                crmSettingRepository.save(userCrmSetting);
                String url = CrmConstants.PIPEDRIVE_COMPANY_DOMAIN + CrmConstants.GET_ALL_CONTACTS;
                Map<String, Object> result = makeApiCall(updatedAcessToken, url);
                ContactsResponse(result, userId, CrmConstants.PIPEDRIVE_PERSON);
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
        } catch (Exception e) {
            LOGGER.warn("Error occured due to " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> getLeadContacts(String userId, Map<String, Object> requestBody) {
        if (!crmSettingRepository.checkByUserId(userId)) {
            LOGGER.info("User's crm setting does not exist for the user");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("Error", "user's crm setting is not found for the user " + userId));
        }
        CrmSettings userCrmSetting = crmSettingRepository.findByUserId(userId);
        LOGGER.info("user has crm Setting");
        String builtUrl = UrlBuilder.urlBuilderWithParam(CrmConstants.PIPEDRIVE_BASE_URL + CrmConstants.PIPEDRIVE_LEADS, requestBody);

        try {
            Date expiryDate = DateTimeUtils.convertDateStringTODate(userCrmSetting.getAccessTokenExpiryDate());
            if (!expiryDate.before(new Date())) {
                LOGGER.info("Token is valid and the built Url is " + builtUrl);
                String accessToken = EncryptionAes.localDecrypt(userCrmSetting.getAccessToken());
                Map<String, Object> result = makeApiCall(accessToken, builtUrl);
                ContactsResponse(result, userId, CrmConstants.PIPEDRIVE_LEAD);
                return ResponseEntity.status(HttpStatus.OK).body(result);

            } else {
                LOGGER.info("Access token has expired. Using refresh token to get access token for the user " + userId);
                JSONObject newCredential = getAccessTokenUsingRefreshToken(userId, EncryptionAes.localDecrypt(userCrmSetting.getRefreshToken()));
                if (newCredential.has("error")) {
                    LOGGER.warn("Error fetching tokens using refresh tokens");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("Error", "Error fetching tokens"));
                }
                LOGGER.info("New credentials: " + newCredential.toString());
                if (!newCredential.has("access_token")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", "access token is not found"));
                }
                String updatedAccessToken = newCredential.getString("access_token");
                CrmHttpUtils.updateCrmSetting(userCrmSetting, newCredential);
                crmSettingRepository.save(userCrmSetting);
                Map<String, Object> result = makeApiCall(updatedAccessToken, builtUrl);
                ContactsResponse(result, userId, CrmConstants.PIPEDRIVE_LEAD);
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
        } catch (Exception e) {
            LOGGER.warn("Error occured due to " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public void ContactsResponse(Map<String, Object> response, String userId, String contactDataType) {
        Object resultData = response.get("data");

        if (resultData instanceof List<?>) {
            List<Map<String, Object>> convertedData = (List<Map<String, Object>>) resultData;
            LOGGER.info("Total contacts: " + convertedData.size());

            for (Map<String, Object> item : convertedData) {
                LOGGER.info("First item: " + item);
                CrmContacts crmContact = new CrmContacts();

                for (Map.Entry<String, Object> entry : item.entrySet()) {
                    LOGGER.info("entry: " + entry);

                    switch (entry.getKey()) {
                        case "id":
                            switch (contactDataType) {
                                case ("PIPEDRIVE_PERSON"):
                                    crmContact.setContactId((int) entry.getValue());
                                    break;
                                case ("PIPEDRIVE_LEAD"):
                                    crmContact.setPipedriveLeadId((String) entry.getValue());
                                    break;
                            }
                            break;
                        case "job_title":
                            crmContact.setJobTitle((String) entry.getValue());
                            break;
                        case "first_name":
                            crmContact.setFirstName((String) entry.getValue());
                            break;
                        case "last_name":
                            crmContact.setLastName((String) entry.getValue());
                            break;
                        case "primary_email":
                            crmContact.setPrimaryEmail((String) entry.getValue());
                            break;
                        case "company_id":
                            crmContact.setCompanyId((int) entry.getValue());
                            break;
                        case "postal_address":
                            crmContact.setPostalAddress((String) entry.getValue());
                            break;
                        case "organization_id":
                            crmContact.setPipedriveLeadOrgId((int) entry.getValue());
                        case "person_id":
                            crmContact.setPipedriveLeadPersonId((int) entry.getValue());
                        case "owner_id":
                            switch (contactDataType) {
                                case ("PIPEDRIVE_PERSON"):
                                    Map<String, Object> ownerDetails = (Map<String, Object>) entry.getValue();
                                    CrmOwnerDetails crmOwnerDetails = new CrmOwnerDetails();
                                    crmOwnerDetails.setOwnerId((int) ownerDetails.get("id"));
                                    crmOwnerDetails.setOwnerName((String) ownerDetails.get("name"));
                                    crmOwnerDetails.setOwnerMail((String) ownerDetails.get("email"));
                                    crmOwnerDetails.setStatus((boolean) ownerDetails.get("active_flag"));
                                    crmContact.setOwnerDetails(crmOwnerDetails);
                                    break;
                                case ("PIPEDRIVE_LEAD"):
                                    crmContact.setPipedriveLeadOwnerId((int) entry.getValue());
                                default:
                                    break;
                            }
                            break;
                        case "phone":
                            List<Map<String, Object>> phoneList = (List<Map<String, Object>>) entry.getValue();
                            List<PhoneData> phones = new ArrayList<>();
                            for (Map<String, Object> phoneEntry : phoneList) {
                                PhoneData phone = new PhoneData();
                                phone.setLabel((String) phoneEntry.get("label"));
                                phone.setValue((String) phoneEntry.get("value"));
                                phone.setPrimary((boolean) phoneEntry.get("primary"));
                                phones.add(phone);
                            }
                            crmContact.setPhoneData(phones);
                            break;
                        case "email":
                            List<Map<String, Object>> emailList = (List<Map<String, Object>>) entry.getValue();
                            List<EmailData> emails = new ArrayList<>();
                            for (Map<String, Object> emailEntry : emailList) {
                                EmailData email = new EmailData();
                                email.setLabel((String) emailEntry.get("label"));
                                email.setValue((String) emailEntry.get("value"));
                                email.setPrimary((boolean) emailEntry.get("primary"));
                                emails.add(email);
                            }
                            crmContact.setEmailData(emails);
                            break;
                        case "active_flag":
                            crmContact.setStatus((boolean) entry.getValue());
                            break;
                    }
                }

                crmContact.setImportType("PIPEDRIVE");
                crmContact.setContactDataType(contactDataType);
                crmContact.setUserId(userId);
                crmContact.setCreateAt(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), null));
                crmContact.setUpdatedAt(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), null));
                crmContactRepository.save(crmContact);
                // Save the contact
                System.out.println("Saving CrmContact: " + crmContact.toString());
                // crmContactRepository.save(crmContact);
            }
        }
    }


    public void LeadContactsResponse(Map<String, Object> response, String userId, String contactDataType) {
        Object resultData = response.get("data");

        if (resultData instanceof List<?>) {
            List<Map<String, Object>> convertedData = (List<Map<String, Object>>) resultData;
            LOGGER.info("Total contacts: " + convertedData.size());

            for (Map<String, Object> item : convertedData) {
                LOGGER.info("First item: " + item);
                CrmContacts crmContact = new CrmContacts();

                for (Map.Entry<String, Object> entry : item.entrySet()) {
                    LOGGER.info("First entry: " + entry);
                    switch (entry.getKey()) {
                        case "id":
                            crmContact.setContactId((int) entry.getValue());
                            break;
                        case "job_title":
                            crmContact.setJobTitle((String) entry.getValue());
                            break;
                        case "first_name":
                            crmContact.setFirstName((String) entry.getValue());
                            break;
                        case "last_name":
                            crmContact.setLastName((String) entry.getValue());
                            break;
                        case "primary_email":
                            crmContact.setPrimaryEmail((String) entry.getValue());
                            break;
                        case "company_id":
                            crmContact.setCompanyId((int) entry.getValue());
                            break;
                        case "postal_address":
                            crmContact.setPostalAddress((String) entry.getValue());
                            break;
                        case "owner_id":
                            switch (contactDataType) {
                                case ("PIPEDRIVE_PERSON"):
                                    Map<String, Object> ownerDetails = (Map<String, Object>) entry.getValue();
                                    CrmOwnerDetails crmOwnerDetails = new CrmOwnerDetails();
                                    crmOwnerDetails.setOwnerId((int) ownerDetails.get("id"));
                                    crmOwnerDetails.setOwnerName((String) ownerDetails.get("name"));
                                    crmOwnerDetails.setOwnerMail((String) ownerDetails.get("email"));
                                    crmOwnerDetails.setStatus((boolean) ownerDetails.get("active_flag"));
                                    crmContact.setOwnerDetails(crmOwnerDetails);
                                    break;
                                case ("PIPEDRIVE_LEAD"):
                                    crmContact.setPipedriveLeadOwnerId((int) entry.getValue());
                                default:
                                    break;
                            }
                            break;
                        case "phone":
                            List<Map<String, Object>> phoneList = (List<Map<String, Object>>) entry.getValue();
                            List<PhoneData> phones = new ArrayList<>();
                            for (Map<String, Object> phoneEntry : phoneList) {
                                PhoneData phone = new PhoneData();
                                phone.setLabel((String) phoneEntry.get("label"));
                                phone.setValue((String) phoneEntry.get("value"));
                                phone.setPrimary((boolean) phoneEntry.get("primary"));
                                phones.add(phone);
                            }
                            crmContact.setPhoneData(phones);
                            break;
                        case "email":
                            List<Map<String, Object>> emailList = (List<Map<String, Object>>) entry.getValue();
                            List<EmailData> emails = new ArrayList<>();
                            for (Map<String, Object> emailEntry : emailList) {
                                EmailData email = new EmailData();
                                email.setLabel((String) emailEntry.get("label"));
                                email.setValue((String) emailEntry.get("value"));
                                email.setPrimary((boolean) emailEntry.get("primary"));
                                emails.add(email);
                            }
                            crmContact.setEmailData(emails);
                            break;
                        case "active_flag":
                            crmContact.setStatus((boolean) entry.getValue());
                            break;
                    }
                }

                crmContact.setImportType("PIPEDRIVE");
                crmContact.setContactDataType(contactDataType);
                crmContact.setUserId(userId);
                crmContact.setCreateAt(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), null));
                crmContact.setUpdatedAt(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), null));
                crmContactRepository.save(crmContact);
                // Save the contact
                System.out.println("Saving CrmContact: " + crmContact.toString());
                // crmContactRepository.save(crmContact);
            }
        }
    }

}
