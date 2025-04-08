package com.connectly_cm.Connectly_CM.buyContacts.service;

import com.connectly_cm.Connectly_CM.buyContacts.model.Contacts;
import com.connectly_cm.Connectly_CM.buyContacts.model.PagedContactResponse;
import com.connectly_cm.Connectly_CM.buyContacts.repository.PurchaseContactsRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PurchaseContactsService {
    @Autowired
    PurchaseContactsRepository purchaseContactsRepository;

    public ResponseEntity<PagedContactResponse> getContacts(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Contacts> contacts = purchaseContactsRepository.findAll(pageable);
        PagedContactResponse pg = new PagedContactResponse();
        pg.setContacts(contacts.getContent());
        pg.setTotalContacts(contacts.getNumberOfElements());
        pg.setCurrentPage(contacts.getNumber());
        pg.setCurrentPage(pageable.getPageNumber());
        return ResponseEntity.status(HttpStatus.FOUND).body(pg);
    }

}
