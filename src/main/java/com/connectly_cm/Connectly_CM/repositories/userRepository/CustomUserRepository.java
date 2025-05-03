package com.connectly_cm.Connectly_CM.repositories.userRepository;

import com.connectly_cm.Connectly_CM.models.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.stereotype.Repository;

@Repository
public class CustomUserRepository {
    @Autowired
    MongoTemplate mongoTemplate;

    public User findByUserId(String userId){
        return mongoTemplate.findById(userId, User.class);
    }
}
