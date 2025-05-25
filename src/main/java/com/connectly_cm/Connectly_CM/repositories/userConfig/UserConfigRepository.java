package com.connectly_cm.Connectly_CM.repositories.userConfig;

import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserConfigRepository extends MongoRepository<UsersConfig, String>, UserConfigRepositoryCustom {
    UsersConfig findByUserId(String userId);
}
