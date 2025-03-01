package com.connectly_cm.Connectly_CM.sendMail.repository;

import com.connectly_cm.Connectly_CM.sendMail.model.ConnectedAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConnectedAccountRepository extends MongoRepository<ConnectedAccount,String> {
    //Optional<ConnectAccount> findByMail(String mail);
    ConnectedAccount findByUserId(String userId);
}
