package com.connectly_cm.Connectly_CM.repositories.contactList;

import com.connectly_cm.Connectly_CM.models.contactList.ContactList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactListRepository extends MongoRepository<ContactList,String> {
}
