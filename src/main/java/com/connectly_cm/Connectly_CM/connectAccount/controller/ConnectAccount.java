package com.connectly_cm.Connectly_CM.connectAccount.controller;

//import statements remain untouched

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@RestController
public class ConnectAccount {

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

    @PostConstruct
    public void init() throws Exception {
        Details web = new Details();
        web.setClientId(clientId);
        web.setClientSecret(clientSecret);
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(GmailScopes.GMAIL_READONLY))
                .setAccessType("offline")
                .build();
    }

    @RequestMapping(value = "/login/gmail", method = RequestMethod.GET)
    public RedirectView googleConnectionStatus() throws Exception {
        return new RedirectView(authorize());
    }

    @RequestMapping(value = "/login/oauth2/code/google", method = RequestMethod.GET, params = "code")
    public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code) {
        JsonObject json = new JsonObject();
        JsonArray arr = new JsonArray();

        try {
            // Exchange the authorization code for an access token
            System.out.println("OAuth Callback: Received Code: " + code);
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            System.out.println("Step 1: OAuth Token Retrieved: " + response.getAccessToken());
            Credential credential = flow.createAndStoreCredential(response, "userID");
            System.out.println("Step 2: Credential Created");
            System.out.println("OAuth Token Response: " + response.toPrettyString());

            // Use the access token to interact with the Gmail API
            Gmail service = new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            System.out.println("Step 3: Gmail Service Initialized");

            // Retrieve the user's email address
            String userEmail = service.users().getProfile("me").execute().getEmailAddress();
            System.out.println("Step 4: User Email Retrieved: " + userEmail);
            flow.createAndStoreCredential(response, userEmail); // Store credentials
            System.out.println("Step 5: Credential Stored");


            // Retrieve emails with the subject "Welcome to A2Cart"
            String userId = "me";
            //String query = "subject:'Welcome to A2Cart'";
            String query = "";
            ListMessagesResponse msgResponse = service.users().messages().list(userId).setQ(query).execute();
            System.out.println("Step 6: Email Query Executed");
            List<Message> messages = msgResponse.getMessages();

            if (messages == null || messages.isEmpty()) {
                System.out.println("Step 7: No Emails Found");
                return new ResponseEntity<>("No messages found", HttpStatus.NO_CONTENT);
            }

            // Retrieve & store email snippets
            for (Message msg : messages) {
                Message message = service.users().messages().get(userId, msg.getId()).execute();
                arr.add(message.getSnippet());
            }
            json.add("response", arr);
            System.out.println("Step 8: Emails Processed");

            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            json.addProperty("error", e.getMessage());
            return new ResponseEntity<>(json.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/login/gmailCallback", method = RequestMethod.GET, params = "code")
    public ResponseEntity<String> oauth2GmailCallback(@RequestParam(value = "code") String code) {
        JsonObject json = new JsonObject();
        JsonArray arr = new JsonArray();

        try {
            // Exchange the authorization code for an access token
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            Credential credential = flow.createAndStoreCredential(response, "userID");

            // Use the access token to interact with the Gmail API
            Gmail service = new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Retrieve emails
            String userId = "me";
            String query = "subject:'Welcome to A2Cart'";
            ListMessagesResponse msgResponse = service.users().messages().list(userId).setQ(query).execute();
            List<Message> messages = msgResponse.getMessages();

            if (messages == null) {
                return new ResponseEntity<>("No messages", HttpStatus.NO_CONTENT);
            }

            // Return email snippets in JSON response
            for (Message msg : messages) {
                Message message = service.users().messages().get(userId, msg.getId()).execute();
                arr.add(message.getSnippet());
            }
            json.add("response", arr);

            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            json.addProperty("error", e.getMessage());
            return new ResponseEntity<>(json.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String authorize() throws Exception {
        if (flow == null) {
            Details web = new Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);

            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(GmailScopes.GMAIL_READONLY)).build();
        }

        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri).set("prompt", "consent");  // Force re-approval
        System.out.println("Authorization URL: " + authorizationUrl.build());
        return authorizationUrl.build();
    }
}
