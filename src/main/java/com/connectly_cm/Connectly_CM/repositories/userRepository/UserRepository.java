package com.connectly_cm.Connectly_CM.repositories.userRepository;

import com.connectly_cm.Connectly_CM.models.users.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsById(String userId);

    User findByapiToken(String apiKey);

    User findByEmail(String userEmail);
}
