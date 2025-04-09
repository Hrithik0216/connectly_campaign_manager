package com.connectly_cm.Connectly_CM.pipedriveIntegration.service;

import com.connectly_cm.Connectly_CM.constants.CrmConstants;
import com.connectly_cm.Connectly_CM.pipedriveIntegration.DTO.PipedriveConnectedResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PipedriveConnectorService {
    private static final Logger LOGGER = Logger.getLogger(PipedriveConnectorService.class);
    @Value("${pipedrive.client.id}")
    private String clientId;
    public ResponseEntity<?> authenticate(String userId) {
        String authenticationUrl = CrmConstants.PIPEDRIVE_OAUTH_URL+"client_id="+clientId+"&redirect_uri="+CrmConstants.PIPEDRIVE_REDIRECT_URL;
        LOGGER.info("The pipedrive authentication url is "+authenticationUrl);
        HashMap<String,String> resp = new HashMap<>();
        resp.put("authorizationUrl",authenticationUrl);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
