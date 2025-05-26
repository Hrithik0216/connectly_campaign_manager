package com.connectly_cm.Connectly_CM.controllers.sequences;

import com.connectly_cm.Connectly_CM.Services.sequences.SequenceServiceLatest;
import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceRequestLatest;
import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.responses.ResultResponse;
import com.connectly_cm.Connectly_CM.utils.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.utils.userUtils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.juli.logging.Log;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sequence")
public class SequenceControllerLatest {

    public static final Logger LOGGER = Logger.getLogger(SequenceControllerLatest.class);

    @Autowired
    SequenceServiceLatest sequenceServiceLatest;

    @Autowired
    UserUtils userUtils;


    @PostMapping("/createSequence")
    public ResponseEntity<?> createSequence(HttpServletRequest request, HttpServletResponse response,
                                            @RequestBody String sequenceName) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            try {
                return sequenceServiceLatest.createSequence(StringUtil.trimString(sequenceName), user.getId());
            } catch (Exception e) {
                LOGGER.error("Error occurred while creating a sequence. " + e.getMessage());
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return new ResponseEntity<>(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            }
        } else {
            LOGGER.info("The user does not exist");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return new ResponseEntity<>(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
        }

    }

    @PostMapping("/configureSequenceData")
    public ResultResponse createSequence(HttpServletResponse response, HttpServletRequest request,
                                         @RequestBody EmailSequenceRequestLatest emailSequenceRequest) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            if (!emailSequenceRequest.getSequenceId().isEmpty() && emailSequenceRequest.getEmailSteps() != null) {
                try {
                    LOGGER.info("User exists. " + user.getId());
                    if (emailSequenceRequest != null) {
                        return sequenceServiceLatest.addDataToSequence(emailSequenceRequest, user.getId());
                    } else {
                        LOGGER.info("Email sequence requestBody not found.");
                        response.setStatus(HttpStatus.BAD_REQUEST.value());
                    }
                } catch (Exception e) {
                    LOGGER.error("Error occurred while creating sequence. " + e.getMessage());
                }
            } else {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }
        } else {
            LOGGER.info("User not found.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
