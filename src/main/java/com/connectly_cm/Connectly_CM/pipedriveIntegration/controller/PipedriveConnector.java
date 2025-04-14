package com.connectly_cm.Connectly_CM.pipedriveIntegration.controller;

import com.connectly_cm.Connectly_CM.Utils.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.pipedriveIntegration.DTO.PipedrivePersonsRequestBody;
import com.connectly_cm.Connectly_CM.pipedriveIntegration.service.PipedriveConnectorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/crm")
public class PipedriveConnector {
    private static final Logger LOGGER = Logger.getLogger(PipedriveConnector.class);


    @Autowired
    PipedriveConnectorService pipedriveConnectorService;

    @PostMapping("pipedrive/authenticate")
    public ResponseEntity<?> authenticate(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");

        if (StringUtil.isEmpty(userId)) {
            LOGGER.info("The userId is not found in headers " + userId + ".");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The userId is not found in headers");
        }
        return pipedriveConnectorService.authenticate(userId);
    }

    @PostMapping("pipedrive/getTokens")
    public ResponseEntity<Map<String, Object>> getTokens(HttpServletRequest request, HttpServletResponse response) {
        String authCode = request.getParameter("authCode");
        if (StringUtil.isEmpty(authCode)) {
            LOGGER.info("The Authorization code is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", "Err"));
        }

        JSONObject result = pipedriveConnectorService.getTokens(authCode);
        if (result.has("error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(result.toMap());
    }

    @GetMapping("pipedrive/getContacts")
    public ResponseEntity<?> getContact(HttpServletRequest request, HttpServletResponse response){
        String userId = request.getHeader("userId");
        LOGGER.info("Header userId: {}"+userId);

        if (userId == null) {
            LOGGER.info("Null UserId"+ userId);
        }

        if (StringUtil.isEmpty(userId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "User ID must be provided in headers or parameters"));
        }

//        Map<String, String> params = new HashMap<>();
//        request.getParameterMap().forEach((key, values) -> {
//            if (!key.equals("userId") && values.length > 0) {
//                params.put(key, values[0]); // Takes first value for each key
//            }
//        });
        return pipedriveConnectorService.getContacts(userId);
    }
}
