package com.umerqureshicodes.backend.config;

// ==================== SecurityConfig.java ====================
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

/**
 * Spring Security configuration
 * Configures password encoding and CORS
 */
@Configuration
public class SecurityConfig {

    /**
     * Password encoder bean
     * Uses BCrypt for secure password hashing
     * Replaces PHP's password_hash() / password_verify()
     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    /**
     * CORS configuration
     * Allows cross-origin requests from frontend
     * UPDATE: Change "*" to your actual frontend URL in production
     */
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // Allow requests from any origin (change to your domain in production)
//        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // add more if needed
//
//        // Allow common HTTP methods
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//
//        // Allow all headers
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//
//        // Allow credentials (cookies for session management)
//        configuration.setAllowCredentials(true);
//
//        // Max age for preflight requests (1 hour)
//        configuration.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}