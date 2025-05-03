package com.connectly_cm.Connectly_CM.controllers.jwt;

import com.connectly_cm.Connectly_CM.utils.jwtUtils.JwtUtils;
import com.connectly_cm.Connectly_CM.models.users.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/jwt")
public class JwtController {
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/secure-data")
    public User getSecureData(HttpServletRequest request, HttpServletResponse response) {
        User user = null;
        if (jwtUtils.isTokenExpired(request.getHeader("Authorization"))) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        try {
            user = jwtUtils.decodeJwt(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (user != null) {
            return user;
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
