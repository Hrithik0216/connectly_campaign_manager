package com.connectly_cm.Connectly_CM.usersUtils.repository;

import com.connectly_cm.Connectly_CM.usersUtils.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsById(String userId);
}
