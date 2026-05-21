package com.carrentalsystem.app.config;

import com.carrentalsystem.app.security.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtRequestFilter jwtRequestFilter;

    // A. Security Filter Chain for the REST API (Stateless with JWT)
    @Bean
    @Order(1) // This chain is evaluated first for matching requests
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // Apply this configuration only to API requests
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public API endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        // Role-based access for API
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasRole("USER")
                        // All other API requests need authentication
                        .anyRequest().authenticated()
                )
                // Crucial: Set session management to STATELESS for the API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Add our custom JWT filter before the standard Spring Security filter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // B. Security Filter Chain for the Web Application (Stateful with Sessions)
    @Bean
    @Order(2) // This chain is evaluated if the first one doesn't match
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public Thymeleaf endpoints
                        .requestMatchers("/", "/auth/login", "/auth/register", "/auth/admin/login", "/error/**").permitAll()
                        // Static resources
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/uploads/**").permitAll()
                        // Role-based access for web pages
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        // Everything else needs authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        return http.build();
    }

    // C. Shared Beans used by both Security Chains

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            boolean isUser = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else if (isUser) {
                response.sendRedirect("/user/dashboard");
            } else {
                response.sendRedirect("/403");
            }
        };
    }
}
