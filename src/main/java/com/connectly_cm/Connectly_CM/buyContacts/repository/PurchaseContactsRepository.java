package com.connectly_cm.Connectly_CM.buyContacts.repository;

import com.connectly_cm.Connectly_CM.buyContacts.model.Contacts;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PurchaseContactsRepository extends MongoRepository<Contacts,String> {
}
