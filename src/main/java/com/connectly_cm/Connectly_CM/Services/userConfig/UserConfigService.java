package com.connectly_cm.Connectly_CM.Services.userConfig;

import com.connectly_cm.Connectly_CM.responses.resultResponses.ErrResponse;
import com.connectly_cm.Connectly_CM.responses.resultResponses.FortuneResponse;
import com.connectly_cm.Connectly_CM.responses.resultResponses.ResultResponse;
import com.connectly_cm.Connectly_CM.constants.ConfigurationConstants;
import com.connectly_cm.Connectly_CM.models.connectInboxModels.UnifiedInboxAccounts;
import com.connectly_cm.Connectly_CM.repositories.connectInboxRepositories.ConnectedUnifiedInboxAccounts;
import com.connectly_cm.Connectly_CM.dtos.userConfig.UserConfiguration;
import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;
import com.connectly_cm.Connectly_CM.repositories.userConfig.UserConfigRepository;
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

    @Autowired
    ConnectedUnifiedInboxAccounts connectedMailAccounts;

    public UsersConfig getUserConfig(String userId) {
        LOGGER.info("Getting The users config");
        UsersConfig userConfig = userConfigRepository.findByUserId(userId);
        return userConfig;
    }

    public ResponseEntity<Object> configureUserConfig(String userId, UserConfiguration userConfiguration) {
        UsersConfig checkForExistingConfig = userConfigRepository.findByUserId(userId);
        UnifiedInboxAccounts connectedAccount = connectedMailAccounts.findByUserId(userId);

        if (connectedAccount == null) {
            LOGGER.info("An account for sending Emails is not connected. Please connect your mail account");
            ErrResponse err = new ErrResponse.Builder("An account for sending Emails is not connected. Please connect your mail account",
                    HttpStatus.NOT_FOUND.value()).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(err);
        }

        if (checkForExistingConfig != null) {
            LOGGER.info("The user already have a config");
            ErrResponse err = new ErrResponse.Builder("The user already have a config",
                    HttpStatus.CONFLICT.value()).build();
            return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(err);
        }

        LOGGER.info("The user does not own a config");
        UsersConfig usersConfig = new UsersConfig();
        usersConfig.setTimeWindow(userConfiguration.getTimeWindow());
        usersConfig.setUserId(userId);
        int delayCheck = checkDelayConfiguration(userConfiguration);

        if (delayCheck == 400) {
            ErrResponse err = new ErrResponse.Builder("The delay cannot be less than 24hrs or greater than 74hrs",
                    HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(err);
        }

        usersConfig.setDelayInSeconds(userConfiguration.getDelayInSeconds());
        usersConfig.setFromAddress(connectedAccount.getConnectedEmailAccounts().get(0).getConnectedMail());
        userConfigRepository.save(usersConfig);
        FortuneResponse result = new FortuneResponse.Builder(HttpStatus.CREATED.value(), usersConfig).setMessage("Saved user config");
        return ResponseEntity.status(HttpStatus.OK.value()).body(result);
    }

    public ResponseEntity<Object> updateConfiguration(String userId, UserConfiguration newUserConfiguration) {
        UsersConfig existingUserConfig = userConfigRepository.findByUserId(userId);
        ResultResponse res = new ResultResponse();
        if (existingUserConfig == null) {
            LOGGER.info("User's config does not exist");
            ErrResponse err = new ErrResponse.Builder("Existing configuration was not found. Please configure your requirements before updating",
                    HttpStatus.BAD_REQUEST.value()).build();
            return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(err);
        }
        LOGGER.info("User's config and new config exist");
        if (newUserConfiguration.getDelayInSeconds() != null) {
            int delaySeconds = checkDelayConfiguration(newUserConfiguration);
            if (delaySeconds == 400) {
                ErrResponse err = new ErrResponse.Builder("The delay cannot be less than 24hrs or greater than 74hrs",
                        HttpStatus.BAD_REQUEST.value()).build();
                return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(err);
            }
        }
        userConfigRepository.updateConfigByFindingFirst(userId, newUserConfiguration);
        FortuneResponse fr = new FortuneResponse.Builder(HttpStatus.OK.value(), newUserConfiguration)
                .setMessage("Updated with the new configuration");
        return ResponseEntity.status(HttpStatus.OK.value()).body(fr);
    }

    public int checkDelayConfiguration(UserConfiguration userConfiguration) {
        if (userConfiguration.getDelayInSeconds() > ConfigurationConstants.THREE_DAYS ||
                userConfiguration.getDelayInSeconds() < ConfigurationConstants.ONE_DAY) {
            return 400;

        } else {
            return 200;
        }
    }
}

