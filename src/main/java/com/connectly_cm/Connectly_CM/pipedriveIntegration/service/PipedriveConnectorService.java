package com.connectly_cm.Connectly_CM.pipedriveIntegration.service;

import com.connectly_cm.Connectly_CM.constants.CrmConstants;
import com.connectly_cm.Connectly_CM.pipedriveIntegration.DTO.PipedriveConnectedResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Service
public class PipedriveConnectorService {
    private static final Logger LOGGER = Logger.getLogger(PipedriveConnectorService.class);

    @Value("${pipedrive.client.id}")
    private String clientId;

    @Value("${pipedrive.client.secret}")
    private String clientSecret;


    public ResponseEntity<?> authenticate(String userId) {
        String authenticationUrl = CrmConstants.PIPEDRIVE_OAUTH_URL + "client_id=" + clientId + "&redirect_uri=" + CrmConstants.PIPEDRIVE_REDIRECT_URL;
        LOGGER.info("The pipedrive authentication url is " + authenticationUrl);
        HashMap<String, String> resp = new HashMap<>();
        resp.put("authorizationUrl", authenticationUrl);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    public ResponseEntity<?> getTokens(String authCode) {
        String getTokenUrl = CrmConstants.PIPEDRIVE_GET_TOKENS_URL;
        String basicAuthCred = clientId + ":" + clientSecret;
        String token64 = new String(Base64.getEncoder().encode(basicAuthCred.getBytes()));
        String auth = CrmConstants.AUTH_TYPE + token64;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, auth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", CrmConstants.PIPEDRIVE_GRANT_TYPE);
        map.add("code", authCode);
        map.add("redirect_uri", CrmConstants.PIPEDRIVE_REDIRECT_URL);


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(getTokenUrl, request, String.class);
            return ResponseEntity.status(HttpStatus.OK).body(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException ex){
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Interal server err");
        }

    }
}
