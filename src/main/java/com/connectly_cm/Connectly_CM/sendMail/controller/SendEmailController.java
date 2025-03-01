package com.connectly_cm.Connectly_CM.sendMail.controller;

import com.connectly_cm.Connectly_CM.sendMail.dto.EmailRequest;
import com.connectly_cm.Connectly_CM.sendMail.service.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendEmailController {
    @Autowired
    SendEmailService sendEmailService;

    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
    public void sendEmail(@RequestBody EmailRequest emailRequest) {
        if (emailRequest == null || emailRequest.getUserId() == null ||
                emailRequest.getToAddress() == null || emailRequest.getSubject() == null ||
                emailRequest.getBody() == null) {
            throw new IllegalArgumentException("Invalid request: All fields are required");
        }
        System.out.println("sendmail controller");
        sendEmailService.sendEmail(emailRequest.getUserId(),
                emailRequest.getToAddress(),
                emailRequest.getSubject(),
                emailRequest.getBody());
    }
}
