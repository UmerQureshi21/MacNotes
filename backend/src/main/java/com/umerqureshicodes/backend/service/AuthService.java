package com.umerqureshicodes.backend.service;

import com.umerqureshicodes.backend.entity.Admin;
import com.umerqureshicodes.backend.entity.User;
import com.umerqureshicodes.backend.repository.AdminRepository;
import com.umerqureshicodes.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Handles authentication logic for users and admins
 * Mirrors: signin.php, signup.php, adminSignIn.php
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
//    private final PasswordEncoder passwordEncoder;

    /**
     * User signin logic
     * Mirrors: signin.php logic
     * Returns: {status: success/fail, message: string, username: string}
     */
    public Map<String, Object> signinUser(String email, String password) {
        String macID = email.split("@")[0];
        Optional<User> userOpt = userRepository.findByMacID(macID);
        Map<String, Object> response = new HashMap<>();

        if (userOpt.isEmpty()) {
            response.put("status", "fail");
            response.put("message", "This MacID doesn't exist! Please create an account first!");
            return response;
        }

        User user = userOpt.get();
        if (!password.equals(user.getPassword())) {
            response.put("status", "fail");
            response.put("message", "Wrong password!");
            return response;
        }

        response.put("status", "success");
        response.put("message", "Logged in successfully");
        response.put("username", user.getUsername());
        return response;
    }

    /**
     * Admin signin logic
     * Mirrors: adminSignIn.php logic
     * Returns: {status: success/fail, message: string, username: string}
     */
    public Map<String, Object> signinAdmin(String email, String password) {
        String macID = email.split("@")[0];
        Optional<Admin> adminOpt = adminRepository.findByMacID(macID);
        Map<String, Object> response = new HashMap<>();

        if (adminOpt.isEmpty()) {
            response.put("status", "fail");
            response.put("message", "This MacID doesn't exist! Please create an account first!");
            return response;
        }

        Admin admin = adminOpt.get();
        if (!password.equals(admin.getPassword())) {
            response.put("status", "fail");
            response.put("message", "Wrong password!");
            return response;
        }

        response.put("status", "success");
        response.put("message", "Logged in successfully");
        response.put("username", admin.getUsername());
        return response;
    }

    /**
     * User signup logic
     * Mirrors: signup.php logic (if it exists in your PHP code)
     * Returns: {status: success/InvPass/fail, message: string}
     * InvPass = Invalid Password (weak password)
     */
    public Map<String, Object> signup(String username, String email, String password) {
        String macID = email.split("@")[0];
        Map<String, Object> response = new HashMap<>();

        // Check if user already exists
        if (userRepository.findByMacID(macID).isPresent()) {
            response.put("status", "fail");
            response.put("message", "User already exists!");
            return response;
        }

        // Validate password strength
        // Must have: at least 8 chars, 1 uppercase letter, 1 number
        if (!isPasswordStrong(password)) {
            response.put("status", "InvPass");
            response.put("message", "Password must be at least 8 characters with uppercase and number!");
            return response;
        }

        // Create and save new user
        User user = new User();
        user.setMacID(macID);
        user.setUsername(username);
        user.setPassword(password);
        user.setNumberUploads(0);
        user.setNumDownloads(0);
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "Account created successfully!");
        return response;
    }

    /**
     * Validates password strength
     * Requirements:
     * - At least 8 characters
     * - At least 1 uppercase letter
     * - At least 1 number
     */
    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");

        return hasUppercase && hasNumber;
    }
}