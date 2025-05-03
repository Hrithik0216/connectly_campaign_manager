package com.connectly_cm.Connectly_CM.controllers.jwt;

import com.connectly_cm.Connectly_CM.ErrResponses.UserDataErrResponse;
import com.connectly_cm.Connectly_CM.Services.users.UserService;
import com.connectly_cm.Connectly_CM.utils.jwtUtils.JwtUtils;
import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.utils.userUtils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/jwt")
public class JwtController {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    UserService userService;
    @Autowired
    UserUtils userUtils;


    @GetMapping("/secure-data")
    public ResponseEntity<?> getSecureData(HttpServletRequest request, HttpServletResponse response) {
        User user = null;
        user = userUtils.getUserData(request);

        if (user != null) {
            Map<String,Object> result = new HashMap<>();
            result.put("responseCode",200);
            result.put("data",user);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserDataErrResponse(HttpStatus.UNAUTHORIZED, "The user does not exists"));
        }
    }
}
