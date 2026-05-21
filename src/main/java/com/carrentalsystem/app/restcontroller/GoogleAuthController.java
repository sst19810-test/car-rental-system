package com.carrentalsystem.app.restcontroller;

import com.carrentalsystem.app.service.impl.GoogleAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;


@Slf4j
@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {
    @Autowired
    GoogleAuthService googleAuthService;


    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code){
        try {
            String jwtToken = googleAuthService.processAuthorizationCode(code);
            return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap("token",jwtToken));
        }
        catch (Exception exception){
            log.error("Error Accured Oauth2");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
