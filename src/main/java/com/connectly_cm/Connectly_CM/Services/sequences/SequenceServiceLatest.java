package com.connectly_cm.Connectly_CM.Services.sequences;

import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceRequestLatest;
import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceStepLatest;
import com.connectly_cm.Connectly_CM.models.sequences.EmailSequenceLatest;
import com.connectly_cm.Connectly_CM.repositories.sequence.EmailSequenceLatestRepository;
import com.connectly_cm.Connectly_CM.responses.CreateSequenceResponse;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model.EmailSequence;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model.EmailSequenceStep;
import com.connectly_cm.Connectly_CM.utils.DateUtils.DateTimeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class SequenceServiceLatest {
    @Autowired
    EmailSequenceLatestRepository emailSequenceLatestRepository;

    private static final Logger LOGGER = Logger.getLogger(SequenceServiceLatest.class);

    public CreateSequenceResponse createSequence(EmailSequenceRequestLatest emailSequenceRequest, String userId) {

        // Save the sequence
        EmailSequenceLatest newSequence = new EmailSequenceLatest();
        newSequence.setUserId(userId);
        newSequence.setFromAddress(emailSequenceRequest.getFromAddress());
        //Getting FromAddress from user's config
//        newSequence.setFromAddress();
        newSequence.setTimeWindow(emailSequenceRequest.getTimeWindow());
        newSequence.setCreatedAt(DateTimeUtils.convertDateToString(new Date(), TimeZone.getTimeZone("UTC"), null));

//        List<EmailSequenceStep> newEmailSeqSteps = emailSequenceRequest.getEmailSteps().stream()
//                .map(emailsStep -> {
//                    EmailSequenceStep step = new EmailSequenceStep();
//                    step.setToEmailAddress(emailsStep.getToEmailAddress());
//                    step.setSubject(emailsStep.getSubject());
//                    step.setBodyText(emailsStep.getBodyText());
//                    step.setDelayInSeconds(emailsStep.getDelayInSeconds());
//                    step.setCreatedAt(new Date());
//                    return step;
//                })
//                .toList();
        List<EmailSequenceStepLatest> newEmailSeqSteps = emailSequenceRequest.getEmailSteps().stream()
                .map(emailStep -> {
                    EmailSequenceStepLatest step = new EmailSequenceStepLatest();
                    step.setCreatedAt(DateTimeUtils.convertDateToString(new Date(),
                            TimeZone.getTimeZone("UTC"), null));
                    step.setCompleted(false);
                    step.setBodyText(emailStep.getBodyText());
                    step.setSubject(emailStep.getSubject());
                    step.setToEmailAddress(emailStep.getToEmailAddress());
//                  step.setDelayInSeconds(emailSequenceRequest.ge);
                    //From address, timewindow and deelay in seconds missing
                    return step;
                }).toList();

        LOGGER.info("Email steps are: " + newEmailSeqSteps.toString());
        newSequence.setEmailSteps(newEmailSeqSteps);
        LOGGER.info("The email sequence to store in DB is: " + newSequence.toString());
        emailSequenceLatestRepository.save(newSequence);
        //emailSequenceStepRepository.saveAll(newEmailSeqSteps);
//        LOGGER.info("Started scheduling email sequence with new email steps");
        // Schedule the email sequence
//        scheduleEmailSequence(newSequence, newEmailSeqSteps);
//        scheduleEmailSequence2(newSequence, newEmailSeqSteps);

        return new CreateSequenceResponse(HttpStatus.CREATED.value(),
                "Email sequence created and scheduled successfully.");
    }
}
