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
        String authHeader = request.getHeader("Authorization");
        if(authHeader==null || !authHeader.startsWith("Bearer")){
            LOGGER.info("Authorization Header is not found");
            return null;
        }

        User user = null;
        String token = authHeader.substring(7);
        LOGGER.info("Token : "+token);
        if (userService.isTokenValid(token)) {
            try {
                LOGGER.info("Valid token");
                user = jwtUtils.decodeJwt(token);
                return user;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("Invalid token");
        return null;
    }
}
