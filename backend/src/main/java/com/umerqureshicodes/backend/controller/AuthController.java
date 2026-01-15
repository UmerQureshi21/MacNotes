package com.umerqureshicodes.backend.controller;


import com.umerqureshicodes.backend.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Handles user authentication (signin, signup, logout)
 * Maps to: signin.php, signup.php, adminSignIn.php, checkAdmin.php, getUser.php
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * User signin endpoint
     * Called by: login.js - signin-btn click
     * Expects: email, password (form-urlencoded)
     * Returns: {status, message, username}
     */
    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signinUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        Map<String, Object> response = authService.signinUser(email, password);

        if ("success".equals(response.get("status"))) {
            String macID = email.split("@")[0];
            session.setAttribute("is_admin", false);
            session.setAttribute("username", response.get("username"));
            session.setAttribute("macID", macID);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * User signup endpoint
     * Called by: login.js - signup-btn click
     * Expects: username, email, password (form-urlencoded)
     * Returns: {status, message}
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        Map<String, Object> response = authService.signup(username, email, password);

        if ("success".equals(response.get("status"))) {
            String macID = email.split("@")[0];
            session.setAttribute("is_admin", false);
            session.setAttribute("username", username);
            session.setAttribute("macID", macID);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Admin signin endpoint
     * Called by: adminlogin.js - signin-btn click
     * Expects: email, password (form-urlencoded)
     * Returns: {status, message, username}
     */
    @PostMapping("/admin/signin")
    public ResponseEntity<Map<String, Object>> signinAdmin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        Map<String, Object> response = authService.signinAdmin(email, password);

        if ("success".equals(response.get("status"))) {
            String macID = email.split("@")[0];
            session.setAttribute("is_admin", true);
            session.setAttribute("username", response.get("username"));
            session.setAttribute("macID", macID);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Check if current user is admin
     * Called by: admin.js, adminsearch.js, adminupload.js, etc.
     * Returns: {is_admin: boolean}
     */
    @GetMapping("/check-admin")
    public ResponseEntity<Map<String, Boolean>> checkAdmin(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("is_admin");
        return ResponseEntity.ok(Map.of("is_admin", isAdmin != null && isAdmin));
    }

    /**
     * Get current user from session
     * Called by: dashboard.js - getUser fetch
     * Returns: {access: boolean, username: string}
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        boolean hasAccess = username != null;

        return ResponseEntity.ok(Map.of(
                "access", hasAccess,
                "username", username
        ));
    }

    /**
     * Logout endpoint
     * Called by: Logout button click
     * Returns: {message}
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}