package com.connectly_cm.Connectly_CM.controllers.jwt;

import com.connectly_cm.Connectly_CM.ErrResponses.UserDataErrResponse;
import com.connectly_cm.Connectly_CM.Services.users.UserService;
import com.connectly_cm.Connectly_CM.constants.CustomErrCode;
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

import java.io.IOException;
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
       user= userUtils.getUserData(request);

        if (user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(CustomErrCode.USER_DETAILS,user));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserDataErrResponse(CustomErrCode.UNAUTHORIZED_USER,"The user does not exists"));
        }
    }
}
