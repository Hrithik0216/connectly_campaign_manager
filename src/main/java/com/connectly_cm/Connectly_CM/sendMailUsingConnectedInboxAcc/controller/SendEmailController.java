package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.controller;

import com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.controller.ConnectAccount;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.EmailRequest;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.service.SendEmailService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendEmailController {
    private static final Logger LOGGER = Logger.getLogger(SendEmailController.class);

    @Autowired
    SendEmailService sendEmailService;

    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest) {
        if (emailRequest == null || emailRequest.getFromAddress() == null ||
                emailRequest.getToAddress() == null || emailRequest.getSubject() == null ||
                emailRequest.getBody() == null) {
            LOGGER.info("Invalid request: All fields are required");
            throw new IllegalArgumentException("Invalid request: All fields are required");
        }
        return sendEmailService.sendEmail(emailRequest.getUserId(), emailRequest.getFromAddress(),
                emailRequest.getToAddress(),
                emailRequest.getSubject(),
                emailRequest.getBody());
    }
}
