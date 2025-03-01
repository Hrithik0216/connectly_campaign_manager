package com.connectly_cm.Connectly_CM.sendMail.service;

import com.connectly_cm.Connectly_CM.sendMail.model.ConnectedAccount;
import com.connectly_cm.Connectly_CM.sendMail.repository.ConnectedAccountRepository;
import com.connectly_cm.Connectly_CM.sendMail.utils.CreateEmail;
import com.connectly_cm.Connectly_CM.sendMail.utils.CreateMessage;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Date;

@Service
public class SendEmailService {

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

    public void sendEmail(String userId, String toEmailAddress, String subject, String bodyText) {
        try {
            System.out.println("sendmail service");
            // Retrieve credentials from MongoDB
            ConnectedAccount connectedAccount = connectedAccountRepository.findByUserId(userId);
            if (connectedAccount == null) {
                throw new RuntimeException("Credentials not found for user: " + userId);
            }

            // Check if the token is expired
            if (connectedAccount.getTokenExpiryTime().before(new Date())) {
                // Refresh the token
                System.out.println("refreshing access token");
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

            // Create and send the email
            MimeMessage email = CreateEmail.createEmail(toEmailAddress, fromEmailAddress, subject, bodyText);
            Message message = CreateMessage.createMessageWithEmail(email);
            service.users().messages().send("me", message).execute();

            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
