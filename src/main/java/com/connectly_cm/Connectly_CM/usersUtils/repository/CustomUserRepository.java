package com.connectly_cm.Connectly_CM.usersUtils.repository;

import com.connectly_cm.Connectly_CM.usersUtils.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class CustomUserRepository {
    @Autowired
    MongoTemplate mongoTemplate;

    public User findByUserId(String userId){
        return mongoTemplate.findById(userId, User.class);
    }
}
