package com.connectly_cm.Connectly_CM.utils.userUtils;

import com.connectly_cm.Connectly_CM.Services.users.UserService;
import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.utils.jwtUtils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Component
public class UserUtils {
    private static final Logger LOGGER = Logger.getLogger(UserUtils.class);
    @Autowired
    UserService userService;
    @Autowired
    JwtUtils jwtUtils;

    public User getUserData(HttpServletRequest request) {
        LOGGER.info(" getUserData");
        User user = null;
        String token = request.getHeader("Authorization");
        LOGGER.info("Token : "+token);
        if (!userService.validateToken(token)) {
            try {
                LOGGER.info("token is valid");
                user = jwtUtils.decodeJwt(request);
                return user;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("null user");
        return null;
    }
}
