package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.repository;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model.EmailSequence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailSequenceRepository extends MongoRepository<EmailSequence, String> {
}
