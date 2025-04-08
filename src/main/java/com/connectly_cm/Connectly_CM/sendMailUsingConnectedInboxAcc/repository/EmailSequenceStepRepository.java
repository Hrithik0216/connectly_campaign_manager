package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.repository;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model.EmailSequenceStep;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailSequenceStepRepository extends MongoRepository<EmailSequenceStep,String> {
}
