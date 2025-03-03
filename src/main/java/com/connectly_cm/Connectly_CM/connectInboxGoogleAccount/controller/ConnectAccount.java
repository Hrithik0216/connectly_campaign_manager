package com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.controller;

//import statements remain untouched

import com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.model.ConnectedGmailAccount;
import com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.model.UnifiedInboxAccounts;
import com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.repository.ConnectedUnifiedInboxAccounts;
import com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.service.ConnectAccountService;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.data.mongodb.core.query.Query;


import javax.annotation.PostConstruct;
import java.util.*;

@RestController
public class ConnectAccount {

    private static final Logger LOGGER = Logger.getLogger(ConnectAccount.class);
    private static final String APPLICATION_NAME = "ConnectlyTesting";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private HttpTransport httpTransport;
    private GoogleAuthorizationCodeFlow flow;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.client.redirect-uri}")
    private String redirectUri;

    @Value("${google.client.scope}")
    private String scope;

    @Autowired
    ConnectedUnifiedInboxAccounts connectedUnifiedInboxAccounts;

    @Autowired
    ConnectAccountService connectAccountService;

    @Autowired
    MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() throws Exception {
        Details web = new Details();
        web.setClientId(clientId);
        web.setClientSecret(clientSecret);
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);
        List<String> scopes = Arrays.asList(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_READONLY);

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                scopes)
                .setAccessType("offline")
                .build();
    }

//    @RequestMapping(value = "/redirect", method = RequestMethod.GET)
//    public RedirectView redirectTogoogele(){
//        return new RedirectView("https://www.google.com");
//    }

    @RequestMapping(value = "/login/gmail", method = RequestMethod.GET)
    public RedirectView googleConnectionStatus(@RequestParam String userId) throws Exception {
        LOGGER.info("Google OAuth initiated...");
        return new RedirectView(authorize(userId));
    }

    @RequestMapping(value = "/login/oauth2/code/google", method = RequestMethod.GET, params = "code")
    public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code,
                                                 @RequestParam(value = "state") String userId) {
        JsonObject json = new JsonObject();
        LOGGER.info("OAuth entered with the userId:" + userId);

        try {
            LOGGER.info("OAuth Callback: Received Code: " + code);
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            LOGGER.info("Step 1: OAuth Token Retrieved: " + response.getAccessToken());
            Credential credential = flow.createAndStoreCredential(response, "userID");
            LOGGER.info("Step 2: Credential Created");
            LOGGER.info("OAuth Token Response: " + response.toPrettyString());

            // Use the access token to interact with the Gmail API
            Gmail service = new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            LOGGER.info("Step 3: Gmail Service Initialized");
            // Retrieve the user's email address
            String userEmail = service.users().getProfile("me").execute().getEmailAddress();
            LOGGER.info("Step 4: User Email Retrieved: " + userEmail);


            UnifiedInboxAccounts isMailConnected = connectedUnifiedInboxAccounts.findByUserId(userId);
            if (isMailConnected != null) {
                List<ConnectedGmailAccount> connectedMails = isMailConnected.getConnectedEmailAccounts();
                boolean isAccfound = connectedMails.stream()
                        .anyMatch(account -> account.getConnectedMail().equals(userEmail));
                if (isAccfound) {
                    LOGGER.info("The Inbox account you are trying to connect already exists. Please remove the account and reconnect. " + userEmail);
                    return ResponseEntity
                            .status(HttpStatus.FOUND)
                            .body("The Inbox account is already connected. Verified with UnifiedInboxAccounts db");
                } else {
                    LOGGER.info("Initiated the process of adding the Inbox account " + userEmail);
                    ConnectedGmailAccount newAcc = new ConnectedGmailAccount();
                    newAcc.setConnectedMail(userEmail.trim());
                    newAcc.setAccessToken(response.getAccessToken());
                    newAcc.setRefreshToken(response.getRefreshToken());
                    newAcc.setScopes(Arrays.asList(
                            "https://www.googleapis.com/auth/gmail.send",
                            "https://www.googleapis.com/auth/gmail.readonly"));
                    newAcc.setTimestamp(new Date());
                    newAcc.setTokenExpiryTime(new Date(System.currentTimeMillis() + response.getExpiresInSeconds() * 1000));
                    Query query = new Query().addCriteria(Criteria.where("userId"));
                    Update updateConnectedMails = new Update().addToSet("connectedEmailAccounts", newAcc);
                    mongoTemplate.upsert(query, updateConnectedMails, UnifiedInboxAccounts.class);
                    LOGGER.info("Updates by adding the inbox acc to the existing document.");
                    return ResponseEntity.status(HttpStatus.OK).body("Connected Inbox mail " + userEmail + " to your acc.");
                }
            }


            ConnectedGmailAccount addNewAcc = new ConnectedGmailAccount();
            addNewAcc.setConnectedMail(userEmail.trim());
            addNewAcc.setAccessToken(response.getAccessToken());
            addNewAcc.setRefreshToken(response.getRefreshToken());
            addNewAcc.setScopes(Arrays.asList(
                    "https://www.googleapis.com/auth/gmail.send",
                    "https://www.googleapis.com/auth/gmail.readonly"));
            addNewAcc.setTimestamp(new Date());
            addNewAcc.setTokenExpiryTime(new Date(System.currentTimeMillis() + response.getExpiresInSeconds() * 1000));
            UnifiedInboxAccounts unifiedInboxAccounts = new UnifiedInboxAccounts();
            unifiedInboxAccounts.setConnectedEmailAccounts(Arrays.asList(addNewAcc));
            unifiedInboxAccounts.setUserId(userId);
            connectedUnifiedInboxAccounts.save(unifiedInboxAccounts);
            LOGGER.info("Added the inbox acc "+ userEmail+" by creating a new document for the userID "+userId);

            json.addProperty("response", "Got credentials");
            json.addProperty("userMail", userEmail);
            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
        } catch (
                Exception e) {
            e.printStackTrace();
            LOGGER.error("Error occured while connecting your google account. The error is: " + e.getMessage());
            json.addProperty("error", e.getMessage());
            return new ResponseEntity<>(json.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/removeAcc", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeAccount(@RequestBody String userId) {
        return connectAccountService.removeAccount(userId);
    }


    private String authorize(String userId) throws Exception {
        if (flow == null) {
            Details web = new Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);

            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(GmailScopes.GMAIL_READONLY)).build();
        }

        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().
                setRedirectUri(redirectUri).
                setState(userId).
                set("prompt", "consent");  // Force re-approval
        LOGGER.info("Authorization URL: " + authorizationUrl.build());
        //System.out.println("Authorization URL: " + authorizationUrl.build());
        return authorizationUrl.build();
    }
}



