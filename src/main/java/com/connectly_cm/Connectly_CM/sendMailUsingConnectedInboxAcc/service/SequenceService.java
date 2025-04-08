package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.service;

import com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.model.ConnectedGmailAccount;
import com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.model.UnifiedInboxAccounts;
import com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.repository.ConnectedUnifiedInboxAccounts;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto.EmailResponse;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.repository.ConnectedAccountRepository;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.utils.CreateEmail;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.utils.CreateMessage;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.coyote.Response;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.*;

@Service
public class SequenceService {
    private static final Logger LOGGER = Logger.getLogger(SequenceService.class);

    @Autowired
    private ConnectedAccountRepository connectedAccountRepository;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Autowired
    private ConnectedUnifiedInboxAccounts connectedUnifiedInboxAccounts;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void refreshAccessToken(String userId, String fromAddress) throws IOException {
        LOGGER.info("Initiating token refresh for user: " + userId + " and email: " + fromAddress);
        UnifiedInboxAccounts unifiedInboxAccount = connectedUnifiedInboxAccounts.findByUserId(userId);

        if (unifiedInboxAccount == null) {
            LOGGER.warn("User not found in database: " + userId);
            throw new RuntimeException("Credentials not found for user: " + userId);
        }

        List<ConnectedGmailAccount> connectedMails = unifiedInboxAccount.getConnectedEmailAccounts();
        Optional<ConnectedGmailAccount> inboxAcc = connectedMails.stream()
                .filter(acc -> acc.getConnectedMail().equals(fromAddress))
                .findFirst();

        if (inboxAcc.isPresent()) {
            ConnectedGmailAccount account = inboxAcc.get();
            LOGGER.info("Found email account: " + fromAddress);

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(new NetHttpTransport())
                    .setJsonFactory(GsonFactory.getDefaultInstance())
                    .setClientSecrets(clientId, clientSecret)
                    .build()
                    .setAccessToken(account.getAccessToken())
                    .setRefreshToken(account.getRefreshToken());

            try {
                if (credential.refreshToken()) {
                    LOGGER.info("Token refreshed successfully for: " + fromAddress);

                    Query query = new Query().addCriteria(Criteria.where("userId").is(userId)
                            .and("connectedEmailAccounts.connectedMail").is(fromAddress));

                    Update update = new Update()
                            .set("connectedEmailAccounts.$.accessToken", credential.getAccessToken())
                            .set("connectedEmailAccounts.$.tokenExpiryTime",
                                    new Date(System.currentTimeMillis() + credential.getExpiresInSeconds() * 1000));

                    mongoTemplate.updateFirst(query, update, UnifiedInboxAccounts.class);
                    LOGGER.info("Updated new access token in database for: " + fromAddress);
                } else {
                    LOGGER.error("Failed to refresh access token for " + fromAddress);
                }
            } catch (IOException e) {
                LOGGER.error("Error refreshing token: {}" + e.getMessage());
                invokeReauthorization(userId, fromAddress);
//                throw new RuntimeException(e);
            }
        } else {
            LOGGER.warn("Email not found in connected accounts: " + fromAddress);
            throw new RuntimeException("Email not connected: " + fromAddress);
        }
    }

    public ResponseEntity<?> invokeReauthorization(String userId, String fromAddress) {
        LOGGER.info("Removing the connected account(" + fromAddress + "). The refresh token has expired for the userID " + userId);
        Query query = new Query(Criteria.where("userId").is(userId));
        mongoTemplate.remove(query, UnifiedInboxAccounts.class);
        LOGGER.info("Deleted the connected account doument");
        /*
         * Deleting the entire account to main atomicity*/
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(fromAddress + "Removed your connected inbox account as the refresh token was expired." +
                        " Please re-connect your account");
    }

    public ResponseEntity<?> sendEmailWithThreadId(String userId, String fromAddress, String toEmailAddress, String subject, String bodyText, String threadId) {
        try {
            LOGGER.info("Sending mail with samethread service is invoked");
            LOGGER.info("Initiating email sending process from " + fromAddress + " to " + toEmailAddress);
            UnifiedInboxAccounts unifiedInboxAccount = connectedUnifiedInboxAccounts.findByUserId(userId);

            if (unifiedInboxAccount == null) {
                LOGGER.warn("User credentials not found for: " + userId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credentials not found.");
            }

            List<ConnectedGmailAccount> connectedMails = unifiedInboxAccount.getConnectedEmailAccounts();
            Optional<ConnectedGmailAccount> inboxAcc = connectedMails.stream()
                    .filter(acc -> acc.getConnectedMail().equals(fromAddress))
                    .findFirst();

            if (inboxAcc.isPresent()) {
                ConnectedGmailAccount account = inboxAcc.get();

                //Check token expiry
                if (account.getTokenExpiryTime().before(new Date())) {
                    LOGGER.info("Access token expired. Refreshing...");
                    refreshAccessToken(userId, fromAddress);

                    // Re-fetch updated credentials
                    UnifiedInboxAccounts updatedAccount = connectedUnifiedInboxAccounts.findByUserId(userId);
                    inboxAcc = updatedAccount.getConnectedEmailAccounts().stream()
                            .filter(acc -> acc.getConnectedMail().equals(fromAddress))
                            .findFirst();

                    if (!inboxAcc.isPresent() || inboxAcc.get().getAccessToken() == null) {
                        LOGGER.error("Token refresh failed, access token still missing.");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token refresh failed.");
                    }
                }

                String accessToken = inboxAcc.get().getAccessToken();
                Credential credential = new GoogleCredential().setAccessToken(accessToken);
                Gmail service = new Gmail.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                        .setApplicationName("Connectly")
                        .build();

                //Validate token email
                String tokenUserEmail = service.users().getProfile("me").execute().getEmailAddress();
                if (!tokenUserEmail.equals(fromAddress)) {
                    LOGGER.error("Access token does not match sender email!");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Token email mismatch. Expected: " + fromAddress + ", Found: " + tokenUserEmail);
                }

                //Check scopes
                if (!account.getScopes().contains("https://www.googleapis.com/auth/gmail.send")) {
                    LOGGER.error("Missing required scope: gmail.send");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient permissions to send email.");
                }

                //Send Email
                MimeMessage email = CreateEmail.createEmail(toEmailAddress, fromAddress, subject, bodyText);
                Message message = CreateMessage.createMessageWithEmail(email);
                message.setThreadId(threadId);
                service.users().messages().send(tokenUserEmail, message).execute();


                LOGGER.info("Email sent successfully from {} to {}" + fromAddress + toEmailAddress);
//                LOGGER.info("message: "+message);

                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("mailResult", Collections.singletonMap("emailResponse", new EmailResponse(fromAddress, toEmailAddress, threadId)));
                return ResponseEntity.status(HttpStatus.OK).body(responseMap);
            }

            LOGGER.warn("Account not found for email: " + fromAddress);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
        } catch (GoogleJsonResponseException e) {
            LOGGER.error("Google API error: " + e.getStatusCode() + e.getDetails());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Google API error: " + e.getDetails().getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error: {}" + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending email: " + e.getMessage());
        }
    }


    public ResponseEntity<?> sendEmail(String userId, String fromAddress, String toEmailAddress, String subject, String bodyText) {
        try {
            LOGGER.info("Sending mail with a new thread");
            LOGGER.info("Initiating email sending process from {} to {}" + fromAddress + toEmailAddress);
            UnifiedInboxAccounts unifiedInboxAccount = connectedUnifiedInboxAccounts.findByUserId(userId);

            if (unifiedInboxAccount == null) {
                LOGGER.warn("User credentials not found for: {}" + userId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credentials not found.");
            }

            List<ConnectedGmailAccount> connectedMails = unifiedInboxAccount.getConnectedEmailAccounts();
            Optional<ConnectedGmailAccount> inboxAcc = connectedMails.stream()
                    .filter(acc -> acc.getConnectedMail().equals(fromAddress))
                    .findFirst();

            if (inboxAcc.isPresent()) {
                ConnectedGmailAccount account = inboxAcc.get();

                //Check token expiry
                if (account.getTokenExpiryTime().before(new Date())) {
                    LOGGER.info("Access token expired. Refreshing...");
                    refreshAccessToken(userId, fromAddress);

                    // Re-fetch updated credentials
                    UnifiedInboxAccounts updatedAccount = connectedUnifiedInboxAccounts.findByUserId(userId);
                    inboxAcc = updatedAccount.getConnectedEmailAccounts().stream()
                            .filter(acc -> acc.getConnectedMail().equals(fromAddress))
                            .findFirst();

                    if (!inboxAcc.isPresent() || inboxAcc.get().getAccessToken() == null) {
                        LOGGER.error("Token refresh failed, access token still missing.");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token refresh failed.");
                    }
                }

                String accessToken = inboxAcc.get().getAccessToken();
                Credential credential = new GoogleCredential().setAccessToken(accessToken);
                Gmail service = new Gmail.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
                        .setApplicationName("Connectly")
                        .build();

                //Validate token email
                String tokenUserEmail = service.users().getProfile("me").execute().getEmailAddress();
                if (!tokenUserEmail.equals(fromAddress)) {
                    LOGGER.error("Access token does not match sender email!");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Token email mismatch. Expected: " + fromAddress + ", Found: " + tokenUserEmail);
                }

                //Check scopes
                if (!account.getScopes().contains("https://www.googleapis.com/auth/gmail.send")) {
                    LOGGER.error("Missing required scope: gmail.send");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient permissions to send email.");
                }

                //Send Email
                MimeMessage email = CreateEmail.createEmail(toEmailAddress, fromAddress, subject, bodyText);
                Message message = CreateMessage.createMessageWithEmail(email);
//                service.users().messages().send(tokenUserEmail, message).execute();
                //New
                Message sentMessage = service.users().messages().send(tokenUserEmail, message).execute();
                LOGGER.info("sentMessae: " + sentMessage);
                LOGGER.info("Message sent! ID: " + sentMessage.getId() + ", Thread ID: " + sentMessage.getThreadId());
                String threadId = sentMessage.getThreadId();
                //

                LOGGER.info("Email sent successfully from {} to {}" + fromAddress + toEmailAddress);
//                LOGGER.info("message: "+message);

                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("mailResult", Collections.singletonMap("emailResponse", new EmailResponse(fromAddress, toEmailAddress, sentMessage.getThreadId())));
                return ResponseEntity.status(HttpStatus.OK).body(responseMap);
            }

            LOGGER.warn("Account not found for email: {}" + fromAddress);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
        } catch (GoogleJsonResponseException e) {
            LOGGER.error("Google API error: {} - {}" + e.getStatusCode() + e.getDetails());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Google API error: " + e.getDetails().getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error: {}" + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending email: " + e.getMessage());
        }
    }

}