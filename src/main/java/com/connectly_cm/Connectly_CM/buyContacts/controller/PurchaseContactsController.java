package com.connectly_cm.Connectly_CM.buyContacts.controller;

import com.connectly_cm.Connectly_CM.buyContacts.service.PurchaseContactsService;
import com.connectly_cm.Connectly_CM.buyContacts.model.PurchaseContactRequestBody;
import com.connectly_cm.Connectly_CM.usersUtils.model.User;
import com.connectly_cm.Connectly_CM.usersUtils.repository.CustomUserRepository;
import com.connectly_cm.Connectly_CM.usersUtils.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buyContacts")
public class PurchaseContactsController {

    private static final Logger LOGGER = Logger.getLogger(PurchaseContactsController.class);
    @Autowired
    PurchaseContactsService purchaseContactsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CustomUserRepository customUserRepository;

    @GetMapping("/contacts")
    public ResponseEntity<?> getContacts(@RequestBody PurchaseContactRequestBody prBody) {
        if (prBody.getUserId().isEmpty()) {
            LOGGER.info("user Id is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user Id is null");
        }
        if (!userRepository.existsById(prBody.getUserId())) {
            LOGGER.info("User not found for the ID: " + prBody.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
        User user = customUserRepository.findByUserId(prBody.getUserId());
        if (!user.isUserStatus()) {
            LOGGER.info("Inactive user: "+prBody.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Inactive user");
        }
        return purchaseContactsService.getContacts(prBody.getPage(), prBody.getLimit());
    }
}
