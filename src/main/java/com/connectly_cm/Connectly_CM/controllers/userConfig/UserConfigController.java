package com.connectly_cm.Connectly_CM.controllers.userConfig;

import com.connectly_cm.Connectly_CM.Services.userConfig.UserConfigService;
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
    public ResponseEntity<?> getUserConfig(HttpServletRequest request,
                                           HttpServletResponse response) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            LOGGER.info("User exists. "+user.getId());
            return userConfigService.getUserConfig(user.getId());

        } else {
            LOGGER.info("Users config does not exist. Please add a new one. ");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not own a config");
        }
    }


    @PostMapping("/createConfig")
    public ResponseEntity<?> configureUserConfig(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                @RequestBody UserConfiguration userConfiguration) {
        User user = userUtils.getUserData(request);
        if (user != null) {
            return userConfigService.configureUserConfig(user.getId(), userConfiguration);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does Not exist");
        }
    }
}
