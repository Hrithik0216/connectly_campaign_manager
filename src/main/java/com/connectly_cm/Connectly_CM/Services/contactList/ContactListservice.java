package com.connectly_cm.Connectly_CM.Services.contactList;

import com.connectly_cm.Connectly_CM.models.contactList.ContactList;
import com.connectly_cm.Connectly_CM.repositories.contactList.ContactListRepository;
import com.connectly_cm.Connectly_CM.utils.DateUtils.DateTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Service
public class ContactListservice {
    @Autowired
    ContactListRepository contactListRepository;

//    @PostConstruct
//    public void decodeJWT ()throws IOException {
//        String token = "eyJhbGciOiJIUzI1NiJ9.eyJhcGlUb2tlbiI6IjY4MTYxYjcwYWM5ZjMxNmM0MWQxMzgxMSIsInJvbGVzIjpbIlJPTEVfVVNFUiIsIlJPTEVfQURNSU4iXSwic3ViIjoiaHJpdGhpa3NoYW5tdTJAZ29vZ2xlLmNvbSIsImlhdCI6MTc0NjI3OTMwNCwiZXhwIjoxNzQ2MzY1NzA0fQ.FBGO93o_jqV_9rGWP0dtaCAew4cYiscCdXFCLS-guGk";
//        String[] split_string = token.split("\\.");
//        String base64EncodedBody = split_string[1];
//        Base64.Decoder base64Url = Base64.getUrlDecoder();
//        Map<String, Object> mapping = new ObjectMapper().readValue(base64Url.decode(base64EncodedBody), HashMap.class);
//
//        System.out.println(mapping.toString());
//    }

    public ResponseEntity<?> createList(String listName, String userId) {
        ContactList contactList = new ContactList();
        contactList.setListName(listName);
        contactList.setUserId(userId);
        contactList.setCreatedAt(DateTimeUtils.convertDateToString(new Date(),  TimeZone.getTimeZone("UTC"), null));
        contactList.setUpdatedAt(DateTimeUtils.convertDateToString(new Date(),TimeZone.getTimeZone("UTC"),null));
        contactListRepository.save(contactList);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("responseCode","success"));
    }
}
