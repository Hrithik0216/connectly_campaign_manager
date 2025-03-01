package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.repository;

import com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.model.ConnectedAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConnectedAccountRepository extends MongoRepository<ConnectedAccount,String> {
    //Optional<ConnectAccount> findByMail(String mail);
    ConnectedAccount findByUserId(String userId);
}
