package com.connectly_cm.Connectly_CM.controllers.userConfig;

import com.connectly_cm.Connectly_CM.Services.userConfig.UserConfigService;
import com.connectly_cm.Connectly_CM.apiResponses.ResultResponse;
import com.connectly_cm.Connectly_CM.dtos.userConfig.UserConfiguration;
import com.connectly_cm.Connectly_CM.models.sequences.UsersConfig;
import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.utils.userUtils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class UserConfigController {
    private static final Logger LOGGER = Logger.getLogger(UserConfigController.class);
    @Autowired
    UserUtils userUtils;

    @Autowired
    UserConfigService userConfigService;

    @GetMapping("/getConfig")
    public ResultResponse getUserConfig(HttpServletRequest request,
                                        HttpServletResponse response) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            ResultResponse res = new ResultResponse();
            try {
                LOGGER.info("User exists. " + user.getId());
                UsersConfig result = userConfigService.getUserConfig(user.getId());
                if (result != null) {
                    res.setData(result);
                    res.setMessage("Fetched your configuration");
                    res.setStatusCode(HttpStatus.OK.value());
                    return res;
                } else {
                    LOGGER.info("User's config does not exist");
                    res.setData(result);
                    res.setStatusCode(HttpStatus.NOT_FOUND.value());
                    res.setMessage("Config was not found. Please do configure your requirements");
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                    return res;
                }
            } catch (Exception e) {
                LOGGER.error("Error fetching user's config. " + e.getMessage());
            }
        } else {
            LOGGER.info("Users does not exist");
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        return null;
    }

    @PostMapping("/createConfig")
    public ResultResponse configureUserConfig(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestBody UserConfiguration userConfiguration) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            try {
                return userConfigService.configureUserConfig(user.getId(), userConfiguration);
            } catch (Exception e) {
                LOGGER.info("Error occured while creating config. " + e.getMessage());
            }
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }

    @PostMapping("/updateConfig")
    public ResultResponse updateConfig(HttpServletRequest request, HttpServletResponse response,
                                       @RequestBody UserConfiguration userConfiguration) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            try {
                return userConfigService.updateConfiguration(user.getId(), userConfiguration);
            } catch (Exception e) {
                LOGGER.info("Error occurred while updating config");
            }
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }

}
