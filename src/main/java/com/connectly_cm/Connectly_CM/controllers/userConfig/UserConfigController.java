package com.connectly_cm.Connectly_CM.controllers.userConfig;

import com.connectly_cm.Connectly_CM.Services.userConfig.UserConfigService;
import com.connectly_cm.Connectly_CM.responses.resultResponses.ErrResponse;
import com.connectly_cm.Connectly_CM.responses.resultResponses.FortuneResponse;
import com.connectly_cm.Connectly_CM.responses.resultResponses.ResultResponse;
import com.connectly_cm.Connectly_CM.dtos.userConfig.UserConfiguration;
import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;
import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.utils.userUtils.UserUtils;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value = "/config")
public class UserConfigController {
    private static final Logger LOGGER = Logger.getLogger(UserConfigController.class);
    @Autowired
    UserUtils userUtils;

    @Autowired
    UserConfigService userConfigService;

    @GetMapping("/getConfig")
    public ResponseEntity<Object> getUserConfig(HttpServletRequest request,
                                                HttpServletResponse response) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            try {
                LOGGER.info("User exists. " + user.getId());
                UsersConfig result = userConfigService.getUserConfig(user.getId());
                if (result != null) {
                    FortuneResponse fr = new FortuneResponse.Builder(HttpStatus.OK.value(), result).build();
                    return ResponseEntity.status(HttpStatus.OK.value()).body(fr);
                } else {
                    LOGGER.info("User's config does not exist");
                    FortuneResponse fr = new FortuneResponse.Builder(HttpStatus.NOT_FOUND.value(), result)
                            .setMessage("User's config is not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(fr);
                }
            } catch (Exception e) {
                LOGGER.error("Error fetching user's config. " + e.getMessage());
                ErrResponse errResponse = new ErrResponse
                        .Builder("Internal server Exception. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .setTimeStamp(new Date()).build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
            }
        } else {
            LOGGER.info("Users does not exist");
            ErrResponse errResponse = new ErrResponse
                    .Builder("Unauthorized user. ", HttpStatus.UNAUTHORIZED.value())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(errResponse);
        }
    }

    @PostMapping("/createConfig")
    public ResponseEntity<Object> configureUserConfig(HttpServletRequest request,
                                                      HttpServletResponse response,
                                                      @RequestBody UserConfiguration userConfiguration) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            try {
                return userConfigService.configureUserConfig(user.getId(), userConfiguration);
            } catch (Exception e) {
                LOGGER.info("Error occured while creating config. " + e.getMessage());
                ErrResponse err = new ErrResponse.Builder("Internal server error",HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(err);
            }
        } else {
            ErrResponse errResponse = new ErrResponse
                    .Builder("Unauthorized user. ", HttpStatus.UNAUTHORIZED.value())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(errResponse);
        }
    }

    @PostMapping("/updateConfig")
    public ResponseEntity<Object> updateConfig(HttpServletRequest request, HttpServletResponse response,
                                               @RequestBody UserConfiguration userConfiguration) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            try {
                return userConfigService.updateConfiguration(user.getId(), userConfiguration);
            } catch (Exception e) {
                ErrResponse errResponse = new ErrResponse
                        .Builder("Internal server Exception. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .setTimeStamp(new Date()).build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
            }
        } else {
            LOGGER.info("Users does not exist");
            ErrResponse errResponse = new ErrResponse
                    .Builder("Unauthorized user. ", HttpStatus.UNAUTHORIZED.value())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(errResponse);
        }
    }

}
