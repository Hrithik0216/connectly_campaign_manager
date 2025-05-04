package com.connectly_cm.Connectly_CM.controllers.pipedrive;

import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.utils.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.Services.pipedrive.PipedriveConnectorService;
import com.connectly_cm.Connectly_CM.utils.userUtils.UserUtils;
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
    UserUtils userUtils;

    @Autowired
    PipedriveConnectorService pipedriveConnectorService;

    @PostMapping("pipedrive/authenticate")
    public ResponseEntity<?> authenticate(HttpServletRequest request, HttpServletResponse response) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            LOGGER.info("The userId is " + user.getId());
            return pipedriveConnectorService.authenticate(user.getId());
        } else {
            LOGGER.warn("The user does not exist");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "The user does not exist"));
        }

    }

    @PostMapping("pipedrive/getTokens")
    public ResponseEntity<Map<String, Object>> getTokens(HttpServletRequest request, HttpServletResponse response) {
        String authCode = request.getParameter("authCode");
        User user = userUtils.getUserData(request);
        if (StringUtil.isEmpty(authCode)) {
            LOGGER.info("The Authorization code is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", "The Authorization code is empty"));
        }
        if (user != null) {
            LOGGER.info("The userId is " + user.getId());
            JSONObject result = pipedriveConnectorService.getTokens(authCode, user.getId());
            if (result.has("error")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "The oauth code has expired"));
            }
            if (result.has("serverErr")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "The oauth code has expired"));
            }
            return ResponseEntity.status(HttpStatus.OK).body(result.toMap());
        } else {
            LOGGER.warn("The user does not exist");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "The user does not exist"));
        }
    }

    @GetMapping("pipedrive/getContacts")
    public ResponseEntity<?> getContact(HttpServletRequest request, HttpServletResponse response) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            LOGGER.info("Header userId: " + user.getId());
            return pipedriveConnectorService.getContacts(user.getId());
        } else {
            LOGGER.info("The userId is empty");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User does not exist"));
        }
    }

    @GetMapping("pipedrive/getLeadContacts")
    public ResponseEntity<?> getLeadContacts(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestBody) {

        User user = userUtils.getUserData(request);
        if (user != null) {
            LOGGER.info("The userId is "+user.getId());
            return pipedriveConnectorService.getLeadContacts(user.getId(), requestBody);
        } else {
            LOGGER.info("The userId is empty or null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User doest not exist"));
        }
    }
}
