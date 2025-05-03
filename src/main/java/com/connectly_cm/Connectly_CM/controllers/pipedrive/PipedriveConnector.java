package com.connectly_cm.Connectly_CM.controllers.pipedrive;

import com.connectly_cm.Connectly_CM.utils.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.Services.pipedrive.PipedriveConnectorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        String userId = request.getHeader("userId");

        if (StringUtil.isEmpty(authCode)) {
            LOGGER.info("The Authorization code is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", "Err"));
        }
        LOGGER.info("The userId is " + userId);
        JSONObject result = pipedriveConnectorService.getTokens(authCode, userId);
        if (result.has("error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(result.toMap());
    }

    @GetMapping("pipedrive/getContacts")
    public ResponseEntity<?> getContact(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        LOGGER.info("Header userId: " + userId);

        if (StringUtil.isEmpty(userId)) {
            LOGGER.info("The userId is empty");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User ID must be provided in headers or parameters"));
        }

        return pipedriveConnectorService.getContacts(userId);
    }

    @GetMapping("pipedrive/getLeadContacts")
    public ResponseEntity<?> getLeadContacts(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestBody) {
        String userId = request.getParameter("userId");
        if (StringUtil.isEmpty(userId)) {
            LOGGER.info("The userId is empty or null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User ID must be provided in headers or parameters"));
        }
        return pipedriveConnectorService.getLeadContacts(userId, requestBody);
    }
}
