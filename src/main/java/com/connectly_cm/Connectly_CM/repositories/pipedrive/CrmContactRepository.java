package com.connectly_cm.Connectly_CM.repositories.pipedrive;

import com.connectly_cm.Connectly_CM.models.pipedrive.CrmContacts;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CrmContactRepository extends MongoRepository<CrmContacts,String> {
}
