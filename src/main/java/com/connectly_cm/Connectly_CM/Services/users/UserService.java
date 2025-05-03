package com.connectly_cm.Connectly_CM.Services.users;

import com.connectly_cm.Connectly_CM.utils.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.repositories.userRepository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    public User findMemberDetails(Map<String, Object> mapping) {
        LOGGER.info("finding member details "+mapping.toString());
        String apiKey = mapping.get("apiToken").toString() != null ? mapping.get("apiToken").toString() : null;
        String userMail = mapping.get("userEmail").toString() != null ? mapping.get("userEmail").toString() : null;
        try {
            if (!StringUtil.isEmpty(apiKey)) {
                LOGGER.info("APi key");
                return userRepository.findByapiToken(apiKey);
            } else if (!StringUtil.isEmpty(userMail)) {
                LOGGER.info("email key");
                return userRepository.findByEmail(userMail);
            } else {
                LOGGER.info("null");
                return null;
            }
        } catch (Exception e) {
            LOGGER.warn("Error fetching user's details. " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public User findByEmail(String userEmail) {
        if (!StringUtil.isEmpty(userEmail)) {
            try {
                return userRepository.findByEmail(userEmail);
            } catch (Exception e) {
                LOGGER.info("Error fetching user's detail. " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public boolean validateToken(String token){
        if(StringUtil.isEmpty(token)){
            return true;
        }
        return false;
    }
 }
