package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.service;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.EmailResponse;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.EmailSequenceRequest;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model.EmailSequence;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model.EmailSequenceStep;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.repository.EmailSequenceRepository;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.repository.EmailSequenceStepRepository;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.utils.TimeWindowUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class EmailSchedulerService {
    private static final Logger LOGGER = Logger.getLogger(EmailSchedulerService.class);

    @Autowired
    private EmailSequenceRepository emailSequenceRepository;

    @Autowired
    private EmailSequenceStepRepository emailSequenceStepRepository;

    @Autowired
    private SequenceService sequenceService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ResponseEntity<?> createSequence(EmailSequenceRequest emailSequenceRequest) {
        LOGGER.info("The RequestBody: " + emailSequenceRequest.toString());
        if (emailSequenceRequest == null || emailSequenceRequest.getUserId() == null ||
                emailSequenceRequest.getFromAddress() == null || emailSequenceRequest.getEmailSteps() == null ||
                emailSequenceRequest.getTimeWindow() == null) {
            LOGGER.info("Invalid request: All fields are required: " + emailSequenceRequest);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: All fields are required");
        }

        // Create and save the sequence
        EmailSequence newSequence = new EmailSequence();
        newSequence.setUserId(emailSequenceRequest.getUserId());
        newSequence.setFromAddress(emailSequenceRequest.getFromAddress());
        newSequence.setTimeWindow(emailSequenceRequest.getTimeWindow());
        newSequence.setCreatedAt(new Date());

        List<EmailSequenceStep> newEmailSeqSteps = emailSequenceRequest.getEmailSteps().stream()
                .map(emailsStep -> {
                    EmailSequenceStep step = new EmailSequenceStep();
                    step.setToEmailAddress(emailsStep.getToEmailAddress());
                    step.setSubject(emailsStep.getSubject());
                    step.setBodyText(emailsStep.getBodyText());
                    step.setDelayInSeconds(emailsStep.getDelayInSeconds());
                    step.setCreatedAt(new Date());
                    return step;
                })
                .toList();

        LOGGER.info("Email steps are: " + newEmailSeqSteps.toString());

        newSequence.setEmailSteps(newEmailSeqSteps);
        LOGGER.info("The email sequence to store in DB is: " + newSequence.toString());
        emailSequenceRepository.save(newSequence);
        //emailSequenceStepRepository.saveAll(newEmailSeqSteps);
        LOGGER.info("Started scheduling email sequence with new email steps");
        // Schedule the email sequence
//        scheduleEmailSequence(newSequence, newEmailSeqSteps);
        scheduleEmailSequence2(newSequence, newEmailSeqSteps);

        return ResponseEntity.status(HttpStatus.CREATED).body("Email sequence created and scheduled successfully.");
    }


//    private void scheduleEmailSequence(EmailSequence emailSequence, List<EmailSequenceStep> emailSequenceSteps) {
//        LOGGER.info("Scheduling email sequence with " + emailSequence.toString());
//        List<EmailSequenceStep> stepsWithSequenceId = emailSequenceSteps.stream().map(step -> {
//            step.setSequenceId(emailSequence.getId());
//            return step;
//        }).toList();
//        emailSequenceStepRepository.saveAll(stepsWithSequenceId);
//        long delaySeconds = 0;
//
//        for (EmailSequenceStep step : emailSequenceSteps) {
//            long initialDelay = step.getDelayInSeconds();
//            scheduler.schedule(() -> {
//                LOGGER.info("Started scheduler service for the step: " + step.toString());
//                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
//                    boolean windowResult = TimeWindowUtils.isWithinTimeWindow(now, emailSequence.getTimeWindow());
//                    LOGGER.info("Window result "+windowResult);
//                if (TimeWindowUtils.isWithinTimeWindow(now, emailSequence.getTimeWindow())) {
//                    LOGGER.info("The step is within the timewindow");
//                    try {
//                        if (emailSequence.getThreadId() == null) {
//                            LOGGER.info("The thread value is null for this email sequence");
//                            // First email in the sequence: send and get Thread ID
//                            ResponseEntity<Object> emailResponseMap = sequenceService.sendEmailWithoutThreadId(
//                                    emailSequence.getUserId(),
//                                    emailSequence.getFromAddress(),
//                                    step.getToEmailAddress(),
//                                    step.getSubject(),
//                                    step.getBodyText()
//                            );
//                            LOGGER.info("The emailResponseMap is " + emailResponseMap.toString());
//                            LOGGER.info("THe emailresponseMap statuscode is " + emailResponseMap.getStatusCode());
//                            if (emailResponseMap.getStatusCode() == HttpStatus.OK) {
//                                LOGGER.info("Got success code " + emailResponseMap.getStatusCode());
//                                HashMap<String, Object> responseBody = (HashMap<String, Object>) emailResponseMap.getBody();
//                                String threadId = (String) responseBody.get("threadId");
//                                LOGGER.info("The threadId is " + threadId);
//
//                                // Update sequence with Thread ID
//                                emailSequence.setThreadId(threadId);
//                                LOGGER.info("The email sequence with threadId " + emailSequence);
//                                emailSequenceRepository.save(emailSequence);
//                            }
//                        } else {
//                            LOGGER.info("The email sequence is scheduled without threadID");
//                            // Subsequent emails: use the stored Thread ID
//                            sequenceService.sendEmailWithThreadId(
//                                    emailSequence.getUserId(),
//                                    emailSequence.getFromAddress(),
//                                    step.getToEmailAddress(),
//                                    step.getSubject(),
//                                    step.getBodyText(),
//                                    emailSequence.getThreadId()
//                            );
//                        }
//
//                        // Update step as completed
//                        step.setCompleted(true);
//                        LOGGER.info("The completed step is " + step.toString());
//                        emailSequenceStepRepository.save(step);
//
//                        // Update sequence's last_processed_at
//                        emailSequence.setLastProcessed(new Date());
//                        LOGGER.info("The completed sequence is " + emailSequence.toString());
//                        emailSequenceRepository.save(emailSequence);
//                    } catch (Exception e) {
//                        // Handle exception (e.g., log the error)
//                        e.printStackTrace();
//                    }
//                } else {
//                    LOGGER.info("Error in window");
//                    ResponseEntity.status(HttpStatus.CONFLICT).body("Need tp write logic for rescheduling mails");
////                    rescheduleEmail(step, emailSequence);
//                }
//            }, delaySeconds + initialDelay, TimeUnit.SECONDS);
//
//            delaySeconds += initialDelay;
//        }
//    }


    private void scheduleEmailSequence2(EmailSequence emailSequence, List<EmailSequenceStep> emailSequenceSteps) {
        LOGGER.info("Scheduling email sequence with " + emailSequence.toString());
        List<EmailSequenceStep> stepsWithSequenceId = emailSequenceSteps.stream().map(step -> {
            step.setSequenceId(emailSequence.getId());
            return step;
        }).toList();
        emailSequenceStepRepository.saveAll(stepsWithSequenceId);
        long delaySeconds = 0;

        for (EmailSequenceStep step : emailSequenceSteps) {
            long initialDelay = step.getDelayInSeconds();
            scheduler.schedule(() -> {
                LOGGER.info("Started scheduler service for the step: " + step.toString());
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
                boolean windowResult = TimeWindowUtils.isWithinTimeWindow(now, emailSequence.getTimeWindow());
                LOGGER.info("Window result "+windowResult);
                if (TimeWindowUtils.isWithinTimeWindow(now, emailSequence.getTimeWindow())) {
                    LOGGER.info("The step is within the timewindow");
                    try {
                        if (emailSequence.getThreadId() == null) {
                            LOGGER.info("The thread value is null for this email sequence");
                            // First email in the sequence: send and get Thread ID
                            ResponseEntity<?> emailResponseMap = sequenceService.sendEmail(
                                    emailSequence.getUserId(),
                                    emailSequence.getFromAddress(),
                                    step.getToEmailAddress(),
                                    step.getSubject(),
                                    step.getBodyText()
                            );

//                            Map<String, Object> responseBody = (Map<String, Object>) emailResponseMap.getBody();
//                            Map<String, Object> mailResult = (Map<String, Object>) responseBody.get("mailResult");
//                            EmailResponse emailResponse = (EmailResponse) mailResult.get("emailResponse");
//
//                            String threadId = emailResponse.getThreadId();
//                            LOGGER.info("Thread ID: " + threadId);

                            LOGGER.info("THe emailresponseMap statuscode is " + emailResponseMap.getStatusCode());
                            if (emailResponseMap.getStatusCode() == HttpStatus.OK) {
                                LOGGER.info("Got success code " + emailResponseMap.getStatusCode());
                                Map<String, Object> responseBody = (Map<String, Object>) emailResponseMap.getBody();
                                Map<String, Object> mailResult = (Map<String, Object>) responseBody.get("mailResult");
                                EmailResponse emailResponse = (EmailResponse) mailResult.get("emailResponse");
                                String threadID = emailResponse.getThreadId();
                                LOGGER.info("The threadId is " + threadID);

                                // Update sequence with Thread ID
                                emailSequence.setThreadId(threadID);
                                LOGGER.info("The email sequence with threadId " + emailSequence);
                                emailSequenceRepository.save(emailSequence);
                            }
                        } else {
                            LOGGER.info("The email sequence is scheduled without threadID");
                            // Subsequent emails: use the stored Thread ID
                            sequenceService.sendEmailWithThreadId(
                                    emailSequence.getUserId(),
                                    emailSequence.getFromAddress(),
                                    step.getToEmailAddress(),
                                    step.getSubject(),
                                    step.getBodyText(),
                                    emailSequence.getThreadId()
                            );
                        }

                        // Update step as completed
                        step.setCompleted(true);
                        LOGGER.info("The completed step is " + step.toString());
                        emailSequenceStepRepository.save(step);

                        // Update sequence's last_processed_at
                        emailSequence.setLastProcessed(new Date());
                        LOGGER.info("The completed sequence is " + emailSequence.toString());
                        emailSequenceRepository.save(emailSequence);
                    } catch (Exception e) {
                        // Handle exception (e.g., log the error)
                        e.printStackTrace();
                    }
                } else {
                    LOGGER.info("Error in window");
                    ResponseEntity.status(HttpStatus.CONFLICT).body("Need tp write logic for rescheduling mails");
//                    rescheduleEmail(step, emailSequence);
                }
            }, delaySeconds + initialDelay, TimeUnit.SECONDS);

            delaySeconds += initialDelay;
        }
    }


}
