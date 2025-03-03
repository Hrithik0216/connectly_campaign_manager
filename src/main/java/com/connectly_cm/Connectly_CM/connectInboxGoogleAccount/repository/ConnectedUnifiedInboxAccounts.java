package com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConnectedUnifiedInboxAccounts extends MongoRepository<com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.model.UnifiedInboxAccounts, String> {
    com.connectly_cm.Connectly_CM.connectInboxGoogleAccount.model.UnifiedInboxAccounts findByUserId(String userId);
}
