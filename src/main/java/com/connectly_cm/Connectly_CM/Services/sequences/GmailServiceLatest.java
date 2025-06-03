package com.connectly_cm.Connectly_CM.Services.sequences;

import com.connectly_cm.Connectly_CM.constants.GoogleConstants;
import com.connectly_cm.Connectly_CM.dtos.sequences.EmailSequenceStepLatest;
import com.connectly_cm.Connectly_CM.models.connectInboxModels.ConnectedGmailAccount;
import com.connectly_cm.Connectly_CM.models.connectInboxModels.UnifiedInboxAccounts;
import com.connectly_cm.Connectly_CM.repositories.connectInboxRepositories.ConnectedUnifiedInboxAccounts;
import com.connectly_cm.Connectly_CM.utils.HttpClientUtils.PipedriveHttpClientUtils.CrmHttpUtils;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.api.services.gmail.model.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import java.util.*;

@Service
public class GmailServiceLatest {
    private static final Logger LOGGER = Logger.getLogger(GmailServiceLatest.class);

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Autowired
    ConnectedUnifiedInboxAccounts connectedUnifiedInboxAccounts;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    String gmailBaseUrl = GoogleConstants.GMAIL_BASE_URL;
    String sendMailUrl = gmailBaseUrl + GoogleConstants.SEND_GMAIL;

    /**
     * step 1:
     * Create a MimeMessage using the parameters provided.
     *
     * @param toEmailAddress   email address of the receiver
     * @param fromEmailAddress email address of the sender, the mailbox account
     * @param subject          subject of the email
     * @param bodyText         body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException - if a wrongly formatted address is encountered.
     */
    public static MimeMessage createEmail(String toEmailAddress,
                                          String fromEmailAddress,
                                          String subject,
                                          String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(fromEmailAddress));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(toEmailAddress));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    /**
     * Step 2:
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException        - if service account credentials file not found.
     * @throws MessagingException - if a wrongly formatted address is encountered.
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private JSONObject getAccessTokenUsingRefreshToken(String refreshToken) {
        JSONObject response = CrmHttpUtils.basicAuthorization(GoogleConstants.GMAIL_REFRESH_TOKEN, refreshToken);
        return response;
    }

    private String checkAccessToken(ConnectedGmailAccount connectedAcc, String fromAddress,
                                    UnifiedInboxAccounts connectedMailAccs) throws MessagingException {
        if (connectedAcc.getTokenExpiryTime().before(new Date())) {
            JSONObject resJson = getAccessTokenUsingRefreshToken(connectedAcc.getRefreshToken());
            if (resJson.has("error") || (!resJson.has("access_token"))) {
                LOGGER.warn("Error fetching tokens using refresh tokens");
                throw new MessagingException("Access token refresh failed for: " + fromAddress);
            }
            LOGGER.info("The refreshToken response is " + resJson.toString());
            String accessToken = resJson.getString("access_token");
            CrmHttpUtils.updateUnifiedAccData(connectedMailAccs, fromAddress, resJson);
            connectedUnifiedInboxAccounts.save(connectedMailAccs);
            return accessToken;
        } else {
            return connectedAcc.getAccessToken();
        }
    }

    public Message sendMail(EmailSequenceStepLatest step, String fromAddress, String userId) throws MessagingException, IOException {
        UnifiedInboxAccounts connectedMailAccs = connectedUnifiedInboxAccounts.findByUserId(userId);
        List<ConnectedGmailAccount> connectedMails = connectedMailAccs.getConnectedEmailAccounts();

        ConnectedGmailAccount connectedAcc = connectedMails.stream()
                .filter(mail -> mail.getConnectedMail().equals(fromAddress))
                .findFirst()
                .orElseThrow(() -> new MessagingException("No connected Gmail account found for: " + fromAddress));

        String accessToken = checkAccessToken(connectedAcc, fromAddress, connectedMailAccs);

        HttpRequestInitializer requestInitializer = httpRequest -> {
            httpRequest.getHeaders().setAuthorization("Bearer " + accessToken);
        };

        Gmail service = new Gmail.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), requestInitializer)
                .setApplicationName(GoogleConstants.APPLICATION_NAME)
                .build();

        MimeMessage mimeMessage = createEmail(step.getToEmailAddress(), fromAddress, step.getSubject(), step.getBodyText());
        Message createdMessage = createMessageWithEmail(mimeMessage);

        try {
            createdMessage = service.users().messages().send("me", createdMessage).execute();
            LOGGER.info("Message sent. ID: " + createdMessage.getId());
            return createdMessage;
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                LOGGER.error("Access denied when sending message: " + error.toString());
            }
            throw e;
        }
    }

}
