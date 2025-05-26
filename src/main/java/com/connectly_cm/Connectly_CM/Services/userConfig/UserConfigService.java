package com.connectly_cm.Connectly_CM.Services.userConfig;

import com.connectly_cm.Connectly_CM.responses.ResultResponse;
import com.connectly_cm.Connectly_CM.constants.ConfigurationConstants;
import com.connectly_cm.Connectly_CM.models.connectInboxModels.UnifiedInboxAccounts;
import com.connectly_cm.Connectly_CM.repositories.connectInboxRepositories.ConnectedUnifiedInboxAccounts;
import com.connectly_cm.Connectly_CM.dtos.userConfig.UserConfiguration;
import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;
import com.connectly_cm.Connectly_CM.repositories.userConfig.UserConfigRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public ResultResponse configureUserConfig(String userId, UserConfiguration userConfiguration) {
        UsersConfig checkForExistingConfig = userConfigRepository.findByUserId(userId);
        UnifiedInboxAccounts connectedAccount = connectedMailAccounts.findByUserId(userId);
        ResultResponse res = new ResultResponse();
        if (connectedAccount == null) {
            LOGGER.info("An account for sending Emails is not connected. Please connect your mail account");
            res.setStatusCode(HttpStatus.NOT_FOUND.value());
            res.setMessage("An account for sending Emails is not connected. Please connect your mail account");
            return res;

        }
        if (checkForExistingConfig == null) {
            LOGGER.info("The user does not own a config");
            UsersConfig usersConfig = new UsersConfig();
            usersConfig.setTimeWindow(userConfiguration.getTimeWindow());
            usersConfig.setUserId(userId);
            res = checkDelayConfiguration(userConfiguration, res);
            if (res.getStatusCode() == 400) {
                return res;
            }
            usersConfig.setDelayInSeconds(userConfiguration.getDelayInSeconds());
            usersConfig.setFromAddress(connectedAccount.getConnectedEmailAccounts().get(0).getConnectedMail());
            userConfigRepository.save(usersConfig);
            res.setStatusCode(HttpStatus.CREATED.value());
            res.setMessage("Saved users config");
            res.setData(usersConfig);
            return res;
        }
        LOGGER.info("The user already have a config");
        res.setStatusCode(HttpStatus.CONFLICT.value());
        res.setMessage("The user already have a config");
        return res;
    }

    public ResultResponse updateConfiguration(String userId, UserConfiguration newUserConfiguration) {
        UsersConfig existingUserConfig = userConfigRepository.findByUserId(userId);
        ResultResponse res = new ResultResponse();
        if (newUserConfiguration != null) {
            if (existingUserConfig != null) {
                LOGGER.info("User's config and new config exist");
                if (newUserConfiguration.getDelayInSeconds() != null) {
                    res = checkDelayConfiguration(newUserConfiguration, res);
                    if (res.getStatusCode() == 400) {
                        return res;
                    } else {
                        userConfigRepository.updateConfigByFindingFirst(userId, newUserConfiguration);
                        res.setMessage("Updated with the new configuration");
                        res.setStatusCode(HttpStatus.OK.value());
                        return res;
                    }
                }
            } else {
                LOGGER.info("User's config does not exist");
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                res.setMessage("Existing configuration was not found. Please configure your requirements before updating");
                return res;
            }
        } else {
            LOGGER.info("User's new config does not exist");
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            res.setMessage("Update Configuration was not found");
            return res;
        }
        return null;
    }

    public ResultResponse checkDelayConfiguration(UserConfiguration userConfiguration, ResultResponse res) {
        if (userConfiguration.getDelayInSeconds() > ConfigurationConstants.THREE_DAYS ||
                userConfiguration.getDelayInSeconds() < ConfigurationConstants.ONE_DAY) {
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            res.setMessage("We don't allow configuring delay for more than 72hrs and less than 24hrs.");
            return res;
        } else {
            res.setStatusCode(HttpStatus.OK.value());
            return res;
        }
    }
}

