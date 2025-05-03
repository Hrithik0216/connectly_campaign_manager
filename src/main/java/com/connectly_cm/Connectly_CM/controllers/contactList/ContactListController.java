package com.connectly_cm.Connectly_CM.controllers.contactList;

import com.connectly_cm.Connectly_CM.Services.contactList.ContactListservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController("/contactList")
public class ContactListController {
    @Autowired
    ContactListservice contactListservice;

}
