package com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.controller;

//import statements remain untouched

import com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.service.ConnectAccountService;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model.ConnectedAccount;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.repository.ConnectedAccountRepository;
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
import com.mongodb.client.result.UpdateResult;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    ConnectedAccountRepository connectedAccountRepository;

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
            // Exchange the authorization code for an access token
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
            ConnectedAccount checkIfUserConnectedConnectedBefore = connectedAccountRepository.findByUserId(userId);
            if(checkIfUserConnectedConnectedBefore!=null){
                if(checkIfUserConnectedConnectedBefore.getConnectedMails().contains(userEmail)){
                    LOGGER.info("The account you are trying to connect already exists. Please remove the account and reconnect. " + userEmail);
                 return ResponseEntity.status(HttpStatus.FOUND).body("The account you are trying to connect already exists. Please remove the account and reconnect");
                }
                LOGGER.info("Earlier this user has connected an account. Found a document in DB. We will update in the same document of id "+
                        checkIfUserConnectedConnectedBefore.getId());
                Query query = new Query().addCriteria(Criteria.where("_id").is(checkIfUserConnectedConnectedBefore.getId()));
                Update updateConnectedMail = new Update().addToSet("connectedMails",userEmail);
                UpdateResult addedNewAcc = mongoTemplate.updateFirst(query,updateConnectedMail,ConnectedAccount.class);
                return ResponseEntity.status(HttpStatus.FOUND).body(addedNewAcc.getUpsertedId().toString());
            }
            ConnectedAccount connectedAccount = new ConnectedAccount();
            connectedAccount.setConnectedMails(Arrays.asList(userEmail));
            connectedAccount.setAccessToken(response.getAccessToken());
            connectedAccount.setRefreshToken(response.getRefreshToken());
            connectedAccount.setScopes(Arrays.asList(
                    "https://www.googleapis.com/auth/gmail.send",
                    "https://www.googleapis.com/auth/gmail.readonly"));
            connectedAccount.setTimestamp(new Date());
            connectedAccount.setTokenExpiryTime(new Date(System.currentTimeMillis() + response.getExpiresInSeconds() * 1000));
            connectedAccount.setUserId(userId);
            connectedAccountRepository.save(connectedAccount);
            json.addProperty("response", "Got credentials");
            json.addProperty("userMail", userEmail);
            LOGGER.info("Step 8: Emails Processed");
            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error occured while connecting your google account. The error is: "+e.getMessage());
            json.addProperty("error", e.getMessage());
            return new ResponseEntity<>(json.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/removeAcc", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeAccount(@RequestBody String userId){
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


//@RequestMapping(value = "/login/oauth2/code/google", method = RequestMethod.GET, params = "code")
//public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code) {
//    JsonObject json = new JsonObject();
////        JsonArray arr = new JsonArray();
//
//    try {
//        // Exchange the authorization code for an access token
//        System.out.println("OAuth Callback: Received Code: " + code);
//        TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
//        System.out.println("Step 1: OAuth Token Retrieved: " + response.getAccessToken());
//        Credential credential = flow.createAndStoreCredential(response, "userID");
//        System.out.println("Step 2: Credential Created");
//        System.out.println("OAuth Token Response: " + response.toPrettyString());
//
//        // Use the access token to interact with the Gmail API
//        Gmail service = new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//        System.out.println("Step 3: Gmail Service Initialized");
//
//        // Retrieve the user's email address
//        String userEmail = service.users().getProfile("me").execute().getEmailAddress();
//        System.out.println("Step 4: User Email Retrieved: " + userEmail);
////            flow.createAndStoreCredential(response, userEmail); // Store credentials
////            System.out.println("Step 5: Credential Stored");
//
//        ConnectedAccount connectedAccount = new ConnectedAccount();
//        connectedAccount.setConnectedMails(Arrays.asList(userEmail));
//        connectedAccount.setAccessToken(response.getAccessToken());
//        connectedAccount.setRefreshToken(response.getRefreshToken());
//        connectedAccount.setScopes(Arrays.asList(
//                "https://www.googleapis.com/auth/gmail.send",
//                "https://www.googleapis.com/auth/gmail.readonly"));
//        connectedAccount.setTimestamp(new Date());
//        connectedAccount.setTokenExpiryTime(new Date(System.currentTimeMillis() + response.getExpiresInSeconds() * 1000));
//        connectedAccountRepository.save(connectedAccount);
//
//
//        // Retrieve emails with the subject "Welcome to A2Cart"
////            String userId = "me";
////            //String query = "subject:'Welcome to A2Cart'";
////            String query = "";
////            ListMessagesResponse msgResponse = service.users().messages().list(userId).setQ(query).execute();
////            System.out.println("Step 6: Email Query Executed");
////            List<Message> messages = msgResponse.getMessages();
//
////            if (messages == null || messages.isEmpty()) {
////                System.out.println("Step 7: No Emails Found");
////                return new ResponseEntity<>("No messages found", HttpStatus.NO_CONTENT);
////            }
//
//        // Retrieve & store email snippets
////            for (Message msg : messages) {
////                Message message = service.users().messages().get(userId, msg.getId()).execute();
////                arr.add(message.getSnippet());
////            }
//        json.addProperty("response", "Got credentials");
//        json.addProperty("userMail", userEmail);
//        System.out.println("Step 8: Emails Processed");
//
//        return new ResponseEntity<>(json.toString(), HttpStatus.OK);
//    } catch (Exception e) {
//        e.printStackTrace();
//        json.addProperty("error", e.getMessage());
//        return new ResponseEntity<>(json.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}

