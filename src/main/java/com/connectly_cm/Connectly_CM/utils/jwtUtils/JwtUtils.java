package com.connectly_cm.Connectly_CM.utils.jwtUtils;

import com.connectly_cm.Connectly_CM.Services.users.UserService;
import com.connectly_cm.Connectly_CM.utils.StringUtils.StringUtil;
import com.connectly_cm.Connectly_CM.models.users.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    private static final Logger LOGGER = Logger.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String SECRET_KEY; // Should be in config

    public static UserService userService;
    @Autowired
    public JwtUtils(UserService userService){
        this.userService=userService;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            LOGGER.error("Error checking token expiry", e);
            return true;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public User decodeJwt(String token) throws IOException {
        if (StringUtil.isEmpty(token)) {
            return null;
        }

        try {
            if (isTokenExpired(token)) {
                LOGGER.warn("Token has expired");
                return null;
            }

            String[] split_string = token.split("\\.");
            String base64EncodedBody = split_string[1];
            Base64.Decoder base64Url = Base64.getUrlDecoder();
            Map<String, Object> mapping = objectMapper.readValue(
                    base64Url.decode(base64EncodedBody),
                    HashMap.class
            );
            LOGGER.info("Decoded JWT payload: " + mapping);
            return userService.findMemberDetails(mapping);
        } catch (Exception e) {
            LOGGER.error("Error decoding JWT", e);
            return null;
        }
    }
}