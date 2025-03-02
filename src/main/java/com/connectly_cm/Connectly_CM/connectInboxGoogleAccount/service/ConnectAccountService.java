package com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.service;

import com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.controller.ConnectAccount;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model.ConnectedAccount;
import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.repository.ConnectedAccountRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class ConnectAccountService {
    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(ConnectAccountService.class);
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
