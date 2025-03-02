package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.service;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.EmailResponse;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model.ConnectedAccount;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.repository.ConnectedAccountRepository;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.utils.CreateEmail;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.utils.CreateMessage;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SendEmailService {

    private static final Logger LOGGER = Logger.getLogger(SendEmailService.class);

    @Autowired
    ConnectedAccountRepository connectedAccountRepository;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;


    public void refreshAccessToken(String userId) throws IOException {
        // Retrieve the existing document from MongoDB
        ConnectedAccount connectedAccount = connectedAccountRepository.findByUserId(userId);
        if (connectedAccount == null) {
            throw new RuntimeException("Credentials not found for user: " + userId);
        }

        // Refresh the token
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(new NetHttpTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setAccessToken(connectedAccount.getAccessToken())
                .setRefreshToken(connectedAccount.getRefreshToken());

        try {
            credential.refreshToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Update the fields in the existing document
        connectedAccount.setAccessToken(credential.getAccessToken());
        connectedAccount.setTokenExpiryTime(new Date(System.currentTimeMillis() + credential.getExpiresInSeconds() * 1000));

        // Save the updated document back to MongoDB
        connectedAccountRepository.save(connectedAccount);
    }

    public ResponseEntity<?> sendEmail(String userId, String toEmailAddress, String subject, String bodyText) {
        try {

            LOGGER.info("Initiated the process of sending mail for " + userId + " to " + toEmailAddress);
            // Retrieve credentials from MongoDB
            ConnectedAccount connectedAccount = connectedAccountRepository.findByUserId(userId);
            if (connectedAccount == null) {
                LOGGER.info("Credentials not found for user: " + userId);
                throw new RuntimeException("Credentials not found for user: " + userId);
            }

            // Check if the token is expired
            if (connectedAccount.getTokenExpiryTime().before(new Date())) {
                // Refresh the token
                LOGGER.info("Refreshing access token for " + userId);
                refreshAccessToken(userId);

                // Re-fetch the updated credentials
                connectedAccount = connectedAccountRepository.findByUserId(userId);
            }

            // Use the access token to authenticate the Gmail API
            Credential credential = new GoogleCredential().setAccessToken(connectedAccount.getAccessToken());
            Gmail service = new Gmail.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("Connectly")
                    .build();

            // Use the stored email address as the "from" address

            String fromEmailAddress = connectedAccount.getConnectedMails().get(0);
            LOGGER.info("Your connected acc from db is " + fromEmailAddress);

            // Create and send the email
            MimeMessage email = CreateEmail.createEmail(toEmailAddress, fromEmailAddress, subject, bodyText);
            Message message = CreateMessage.createMessageWithEmail(email);
            service.users().messages().send("me", message).execute();

            LOGGER.info("Email sent successfully to " + toEmailAddress + " from " + fromEmailAddress);
            EmailResponse emailResponse = new EmailResponse(fromEmailAddress,toEmailAddress);

            Map<String, Object> responseMap= new HashMap<>();
            responseMap.put("mailResult", Collections.singletonMap("emailResponse",emailResponse));
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured while sending mail to "+toEmailAddress+". The error is"+e.getMessage());
        }
    }
}
