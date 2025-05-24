package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.controller;

import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.EmailSequenceRequest;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.service.EmailSchedulerService;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.service.SequenceService;
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
@RequestMapping("api/email")
public class SequenceController {

    public static final Logger LOGGER = Logger.getLogger(SequenceController.class);

    @Autowired
    EmailSchedulerService emailSchedulerService;

    @Autowired
    UserUtils userUtils;

    @PostMapping("/createSequence")
    public ResponseEntity<?> createsequence(HttpServletRequest request, HttpServletResponse response,
                                            @RequestBody EmailSequenceRequest emailSequenceRequest){
        User user = userUtils.getUserData(request);

        if(emailSequenceRequest==null){
            LOGGER.error("Invalid Email sequence is sent inside the requestbody");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The mail sequence is invalid");
//            return response.setStatus(HttpServletResponse.SC_ACCEPTED);
        }
        if(emailSequenceRequest.getFromAddress().isEmpty() || emailSequenceRequest.getFromAddress()==null){
            LOGGER.error("From email address is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The from email address is empty");
        }
        if(emailSequenceRequest.getEmailSteps().isEmpty() || emailSequenceRequest.getEmailSteps()==null){
            LOGGER.error("Email steps list is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email steps list is empty");
        }
        if(emailSequenceRequest.getTimeWindow().isEmpty() || emailSequenceRequest.getTimeWindow()==null){
            LOGGER.error("getTimeWindow is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("getTimeWindow is empty");
        }
        return emailSchedulerService.createSequence(emailSequenceRequest);
    }

}
