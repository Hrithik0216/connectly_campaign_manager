package com.connectly_cm.Connectly_CM.pipedriveIntegration.service;

import com.connectly_cm.Connectly_CM.Utils.UrlBuilder.UrlBuilder;
import com.connectly_cm.Connectly_CM.constants.CrmConstants;
import com.connectly_cm.Connectly_CM.Utils.usersUtils.repository.UserRepository;
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
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


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

    public JSONObject getTokens(String authCode) {
        LOGGER.info("Getting tokens for the auth code: " + authCode);
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
            return new JSONObject(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            LOGGER.warn("Error caused due to " + ex.getMessage());
            return new JSONObject().put("error", ex.getMessage());
        } catch (Exception e) {
            LOGGER.warn("Internal server error" + e.getMessage());
            return new JSONObject().put("error", "Internal server err: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getContacts(String userId) {
        if (userRepository.existsById(userId)) {
            String accessToken = "v1u:AQIBAHj-LzTNK2yuuuaLqifzhWb9crUNKTpk4FlQ9rjnXqp_6AH1xWIuX4UNV4pLjxXmWX9qAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMFHdktw7w7f0Pjg7rAgEQgDvdZiq5D_z3NrqUDbPJtST4-2TOMCW6wX9bysOeNz1dnXk2iat6N4tJCtsyTenFd4dHuS53Kg7r436P0Q:2n5mWDY6NhiKJlCHAO_Y9ETUxMytki7Zojhqle2iN44d_NXBXfrBIVzOE0rLfOwNV7GMMChnZrcWhg-gzbEWlWxLDrLTSK3HHKKTnswsxTw5mc4MMOFhFcU8OerbjbaBUrVZBGbNdbi0L6iVkd_CG_fRTMwaA2Ao0R5GS0ghAQcAeeSGRi92yjU3Jl0zWL-9jSdpU-1ljT9-LWtPiSyDNirhW3E_VAWdzZrrZSalG9HaGzwVGenAoWYjt63Rhp3sRdp8N-SqWsVsFPEARDiTMPUuSReQ22yQNyEDQRhuFeQ7yULJbrnnPWFxnqMkmG2vIizfqliFQjYF2hC-OdBtc1QPwGJTxhyiHoq4sflTlz-AiIDGHUIofiK2gEhXRYXNMApVCugAxp8EuMNw2WjAZEug";
            String url = CrmConstants.PIPEDRIVE_COMPANY_DOMAIN+CrmConstants.GET_ALL_CONTACTS;
            makeApiCall(accessToken, url);
            return ResponseEntity.status(HttpStatus.OK).body("Succes");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Failed");
    }

    private void makeApiCall(String accessToken, String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer "+accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            // Print the response status code and body
            System.out.println("Status code: " + response.code());
            System.out.println("Response body: " + response.body().string());
        } catch (IOException e) {
            e.getMessage();
        }
    }
}
