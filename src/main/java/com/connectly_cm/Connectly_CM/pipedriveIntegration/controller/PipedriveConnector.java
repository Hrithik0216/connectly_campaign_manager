package com.connectly_cm.Connectly_CM.pipedriveIntegration.controller;

import com.connectly_cm.Connectly_CM.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.pipedriveIntegration.DTO.PipedriveConnectedResponse;
import com.connectly_cm.Connectly_CM.pipedriveIntegration.service.PipedriveConnectorService;
import com.connectly_cm.Connectly_CM.usersUtils.repository.UserRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crm")
public class PipedriveConnector {
    private static final Logger LOGGER = Logger.getLogger(PipedriveConnector.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    PipedriveConnectorService pipedriveConnectorService;

    @PostMapping("pipedrive/authenticate")
    public ResponseEntity<?> authenticate(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");

        if (StringUtil.isEmpty(userId)) {
            LOGGER.info("The userId is not found in headers " + userId + ".");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The userId is not found in headers");
        }

        try {
            if (userRepository.existsById(userId)) {
                return pipedriveConnectorService.authenticate(userId);
            } else {
                LOGGER.info("The userId is not found in db " + userId + ".");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("The user with userId " + userId + " not found in DB");
            }
        } catch (Exception e) {
            LOGGER.info("Error in getting pipedrive authentication URL");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected err has happened while getting authorization URL " + e.getMessage());
        }
    }

}
