package com.connectly_cm.Connectly_CM.pipedriveIntegration.controller;

import com.connectly_cm.Connectly_CM.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.pipedriveIntegration.service.PipedriveConnectorService;
import com.connectly_cm.Connectly_CM.usersUtils.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            HashMap<String, Object> map = new HashMap<>();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Map<String, Object>) map.put("Error", new String("Err")));
        }

        JSONObject result = pipedriveConnectorService.getTokens(authCode);
        return ResponseEntity.status(HttpStatus.OK).body(result.toMap());
    }
}
