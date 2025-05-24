package com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.service;

import com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.controller.ConnectAccount;
import com.connectly_cm.Connectly_CM.controllers.connectInboxAccount.ConnectInboxAccountController;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model.ConnectedAccount;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.repository.ConnectedAccountRepository;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.GmailScopes;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class ConnectAccountService {
    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(ConnectAccountService.class);
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

    /*
    * The remove method is not working. Need to change UML diagram
    * */
    public ResponseEntity<?> removeAccount(String userId) {
        LOGGER.info("Removing acc for the userid: " + userId);
        ConnectedAccount acc = connectedAccountRepository.findByUserId(userId);
        LOGGER.info("The acc is "+acc);

        if (acc != null) {
            connectedAccountRepository.deleteById(acc.getId());
            return ResponseEntity.status(HttpStatus.OK).body("The connected account has been removed");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

}
