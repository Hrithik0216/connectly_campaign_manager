package com.connectly_cm.Connectly_CM.controllers.sequences;

import com.connectly_cm.Connectly_CM.Services.sequences.SequenceServiceLatest;
import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceRequestLatest;
import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.responses.CreateSequenceResponse;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.controller.SequenceController;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.EmailSequenceRequest;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.service.EmailSchedulerService;
import com.connectly_cm.Connectly_CM.utils.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.utils.userUtils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sequence")
public class SequenceControllerLatest {

    public static final Logger LOGGER = Logger.getLogger(SequenceControllerLatest.class);

    @Autowired
    SequenceServiceLatest sequenceServiceLatest;

    @Autowired
    UserUtils userUtils;

    @PutMapping("/createSequence")
    public CreateSequenceResponse createSequence(HttpServletResponse response, HttpServletRequest request,
                                                 @RequestBody EmailSequenceRequestLatest emailSequenceRequest) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            LOGGER.info("User is not null. " + user.getId());
            if (emailSequenceRequest != null ||
                    !emailSequenceRequest.getEmailSteps().isEmpty() ||
                    !StringUtil.isEmpty(emailSequenceRequest.getFromAddress()) ||
                    emailSequenceRequest.getTimeWindow() != null) {
                return sequenceServiceLatest.createSequence(emailSequenceRequest, user.getId());
            } else {
                LOGGER.info("Email sequence requestBody not found.");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            }
        } else {
            LOGGER.info("User not found.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
