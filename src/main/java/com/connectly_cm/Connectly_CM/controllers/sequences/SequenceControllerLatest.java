package com.connectly_cm.Connectly_CM.controllers.sequences;

import com.connectly_cm.Connectly_CM.Services.sequences.SequenceServiceLatest;
import com.connectly_cm.Connectly_CM.dtos.sequences.ActivateDeactivateSeq;
import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceRequestLatest;
import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.responses.resultResponses.ErrResponse;
import com.connectly_cm.Connectly_CM.responses.resultResponses.ResultResponse;
import com.connectly_cm.Connectly_CM.utils.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.utils.userUtils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            if (!StringUtil.isEmpty(sequenceName)) {
                try {
                    return sequenceServiceLatest.createSequence(StringUtil.trimString(sequenceName), user.getId());
                } catch (Exception e) {
                    LOGGER.error("Error occurred while creating a sequence. " + e.getMessage());
                    ErrResponse er = new ErrResponse.Builder("Error occurred while creating a sequence. " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(er);
                }
            } else {
                ErrResponse er = new ErrResponse.Builder("sequenceName is empty", HttpStatus.BAD_REQUEST.value()).build();
                LOGGER.info("sequenceName is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(er);
            }
        } else {
            ErrResponse er = new ErrResponse.Builder("User does not exists", HttpStatus.UNAUTHORIZED.value()).build();
            LOGGER.info("The user does not exist");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(er);
        }

    }

    @PostMapping("/configureSequenceData")
    public ResponseEntity<?> createSequence(HttpServletResponse response, HttpServletRequest request,
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
                        ErrResponse er = new ErrResponse.Builder("Email sequence requestBody not found.",
                                HttpStatus.BAD_REQUEST.value()).build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(er);
                    }
                } catch (Exception e) {
                    LOGGER.error("Error occurred while creating sequence. " + e.getMessage());
                    ErrResponse er = new ErrResponse.Builder("Error occurred while creating sequence. Internal server error",
                            HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(er);
                }
            } else {
                ErrResponse er = new ErrResponse.Builder("User does not exists", HttpStatus.BAD_REQUEST.value()).build();
                LOGGER.info("The user does not exist");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(er);
            }
        } else {
            ErrResponse er = new ErrResponse.Builder("User does not exists", HttpStatus.UNAUTHORIZED.value()).build();
            LOGGER.info("The user does not exist");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(er);
        }
    }

    @PostMapping("/activateSequence")
    public ResponseEntity<?> activateDeactivateSequence(HttpServletRequest request, HttpServletResponse response,
                                                        @RequestBody ActivateDeactivateSeq activateDeactivateSeq) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            if (!StringUtil.isEmpty(activateDeactivateSeq.getSeqId()) && activateDeactivateSeq.getSeqStatus() != null) {
                try {
                    return sequenceServiceLatest.activateDeactivateSequence(activateDeactivateSeq, user.getId());
                } catch (Exception e) {
                    LOGGER.error("Error occurred while activating a sequence. " + e.getMessage());
                    ErrResponse er = new ErrResponse.Builder("Error occurred while activating a sequence.", HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(er);
                }
            } else {
                LOGGER.info("SequenceId or seq status is empty");
                ErrResponse er = new ErrResponse.Builder("SequenceId or seq status is empty", HttpStatus.BAD_REQUEST.value()).build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(er);
            }
        } else {
            ErrResponse er = new ErrResponse.Builder("User does not exists", HttpStatus.UNAUTHORIZED.value()).build();
            LOGGER.info("The user does not exist");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(er);
        }
    }
}
