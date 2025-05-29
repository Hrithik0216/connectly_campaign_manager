package com.connectly_cm.Connectly_CM.controllers;

import com.connectly_cm.Connectly_CM.responses.exceptionResponses.ExceptionResponse;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class Test {
    private static final Logger LOGGER = Logger.getLogger(Test.class);
    @GetMapping("/heelo")
    public ResponseEntity<?> send() {
        LOGGER.info("Hellow method called");
        throw new RuntimeException("Hellow");
    }

    @ExceptionHandler
    public ResponseEntity<?> userThrownException(RuntimeException e){
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(),
                e.getMessage(),e.getLocalizedMessage());
        return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .body(exceptionResponse);
    }
}
