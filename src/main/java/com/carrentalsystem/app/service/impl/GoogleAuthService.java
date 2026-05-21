package com.carrentalsystem.app.service.impl;

import com.carrentalsystem.app.entity.User;
import com.carrentalsystem.app.helper.Role;
import com.carrentalsystem.app.repository.UserRepository;
import com.carrentalsystem.app.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
public class  GoogleAuthService {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    public String processAuthorizationCode(String code) {
        String tokenEndpoint = "https://oauth2.googleapis.com/token";

        // Prepare request parameters
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("code", code);
        param.add("client_id", clientId);
        param.add("client_secret", clientSecret);
        param.add("redirect_uri", "https://developers.google.com/oauthplayground");
        param.add("grant_type", "authorization_code");
        param.add("code", code);


        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);

        // Exchange code for tokens
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
        if (tokenResponse.getStatusCode() != HttpStatus.OK || tokenResponse.getBody() == null) {
            throw new RuntimeException("Failed to get token from Google");
        }

        String idToken = (String) tokenResponse.getBody().get("id_token");
        if (idToken == null) {
            throw new RuntimeException("No ID token found in Google response");
        }

        // Get user info from ID token
        String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
        if (userInfoResponse.getStatusCode() != HttpStatus.OK || userInfoResponse.getBody() == null) {
            throw new RuntimeException("Failed to retrieve user info from Google");
        }

        Map<String, Object> userInfo = userInfoResponse.getBody();
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name"); // <-- Extracted name
        String picture = (String) userInfo.get("picture"); // optional

        if (email == null) {
            throw new RuntimeException("Email not found in Google user info");
        }

        // Load or create user
        UserDetails userDetails;
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setName(name != null ? name : "Google User");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // random password
            user.setRole(Role.USER);
            userRepository.save(user);
        }

        // Load user details for Spring Security
        userDetails = userDetailsService.loadUserByUsername(email);

        // Authenticate user
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT
        return jwtUtil.generateToken(email);
    }


    public String processAuthorizationCode2(String code) {
        String tokenEndpoint = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("code", code);
        param.add("client_id", clientId);
        param.add("client_secret", clientSecret);
        param.add("redirect_uri", "https://developers.google.com/oauthplayground");
        param.add("grant_type", "authorization_code");
        param.add("code", code);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, header);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
        String idToken = (String) tokenResponse.getBody().get("id_token");
        String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
        UserDetails userDetails = null;
        String jwtToken = null;
        if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> userInfo = userInfoResponse.getBody();
            String email = (String) userInfo.get("email");

            try {
                userDetails = userDetailsService.loadUserByUsername(email);
            }
            catch (Exception e) {
                User user = new User();
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setRole(Role.USER);
                userRepository.save(user);
            }

            UsernamePasswordAuthenticationToken authentication  =
                    new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwtToken = jwtUtil.generateToken(email);
        }
        return jwtToken;

    }
}