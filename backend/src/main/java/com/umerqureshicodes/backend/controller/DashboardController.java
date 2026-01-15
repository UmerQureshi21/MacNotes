package com.umerqureshicodes.backend.controller;

import com.umerqureshicodes.backend.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Handles dashboard operations
 * Maps to: dashboard.php, trending.php
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    /**
     * Get user dashboard stats
     * Called by: dashboard.js - setInterval
     * Returns: {numberOfUploads: number, numberOfDownloads: number, userAverageRating: double}
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No active session"));
        }

        Map<String, Object> dashboard = dashboardService.getUserDashboard(username);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get trending/featured files
     * Called by: dashboard.js - setInterval for "Featured & Trending Resources" carousel
     * Returns: Array of top 10 files sorted by download count
     * Format: [{filename, filetitle, coursecode, description, rating, download-number}, ...]
     */
    @GetMapping("/trending")
    public ResponseEntity<List<Map<String, Object>>> getTrendingFiles() {
        List<Map<String, Object>> trending = dashboardService.getTrendingFiles();
        return ResponseEntity.ok(trending);
    }
}