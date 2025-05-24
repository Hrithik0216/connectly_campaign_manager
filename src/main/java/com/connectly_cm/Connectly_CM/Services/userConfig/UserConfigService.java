package com.connectly_cm.Connectly_CM.Services.userConfig;

import com.connectly_cm.Connectly_CM.dtos.userConfig.UserConfiguration;
import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;
import com.connectly_cm.Connectly_CM.repositories.userConfig.UserConfigRepository;
import org.apache.catalina.startup.UserConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserConfigService {
    private static final Logger LOGGER = Logger.getLogger(UserConfigService.class);
    @Autowired
    UserConfigRepository userConfigRepository;

    public ResponseEntity<?> getUserConfig(String userId) {
        LOGGER.info("Getting The users config");
        UsersConfig userConfig=userConfigRepository.findByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userConfig);
    }

    public ResponseEntity<?> configureUserConfig(String userId, UserConfiguration userConfiguration) {
        UsersConfig checkForExistingConfig = userConfigRepository.findByUserId(userId);
        if(checkForExistingConfig==null){
            LOGGER.info("The user does not own a config");
            UsersConfig usersConfig = new UsersConfig();
            usersConfig.setTimeWindow(userConfiguration.getTimeWindow());
            usersConfig.setUserId(userId);
            usersConfig.setDelayInSeconds(userConfiguration.getDelayInSeconds());
            usersConfig.setFromAddress(userConfiguration.getFromAddress());
            userConfigRepository.save(usersConfig);
            return ResponseEntity.status(HttpStatus.CREATED).body("Saved users config");
        }else{
            LOGGER.info("The user already have a config");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The user already have a config");
        }
    }
}
