package com.connectly_cm.Connectly_CM.repositories.connectInboxRepositories;

import com.connectly_cm.Connectly_CM.models.connectInboxModels.UnifiedInboxAccounts;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConnectedUnifiedInboxAccounts extends MongoRepository<UnifiedInboxAccounts, String> {
    UnifiedInboxAccounts findByUserId(String userId);
}
