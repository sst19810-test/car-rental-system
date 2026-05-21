package com.carrentalsystem.app.restcontroller;

import com.carrentalsystem.app.dto.UserDTO;
import com.carrentalsystem.app.dto.UserLoginDTO;
import com.carrentalsystem.app.entity.User;
import com.carrentalsystem.app.helper.Role;
import com.carrentalsystem.app.dto.AuthRequest;
import com.carrentalsystem.app.dto.AuthResponse;
import com.carrentalsystem.app.security.util.JwtUtil;
import com.carrentalsystem.app.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userService.isEmailExists(user.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email already exists!");
        }

        user.setRole(Role.USER);
        UserDTO savedUser = userService.registerUser(user); // Handles password encoding
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }

    // This single endpoint handles login for both USER and ADMIN roles via API
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            // Authenticate using Spring Security's AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }

        // If authentication is successful, load UserDetails
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.username());

        // Generate the JWT
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        // Return the JWT in the response
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
