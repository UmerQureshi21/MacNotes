package com.umerqureshicodes.backend.service;

import com.umerqureshicodes.backend.entity.MFile;
import com.umerqureshicodes.backend.entity.User;
import com.umerqureshicodes.backend.repository.MFileRepository;
import com.umerqureshicodes.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles dashboard operations
 * Mirrors: dashboard.php, trending.php
 */
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final MFileRepository mFileRepository;

    /**
     * Get user dashboard stats
     * Mirrors: dashboard.php
     * Returns: {
     *   numberOfUploads: number,
     *   numberOfDownloads: number,
     *   userAverageRating: double (rounded to 1 decimal place)
     * }
     */
    public Map<String, Object> getUserDashboard(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        Map<String, Object> response = new HashMap<>();

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Get upload and download counts
        response.put("numberOfUploads", user.getNumberUploads() != null ? user.getNumberUploads() : 0);
        response.put("numberOfDownloads", user.getNumDownloads() != null ? user.getNumDownloads() : 0);

        // Calculate average rating of user's uploaded files
        List<MFile> files = mFileRepository.findByMacID(user.getMacID());
        Double avgRating = files.stream()
                .mapToDouble(f -> f.getRating() != null ? f.getRating() : 0.0)
                .average()
                .orElse(0.0);

        // Round to 1 decimal place
        double roundedRating = Math.round(avgRating * 10.0) / 10.0;
        response.put("userAverageRating", roundedRating);

        return response;
    }

    /**
     * Get trending/featured files
     * Mirrors: trending.php (implied functionality from dashboard.js)
     * Returns: Top 10 files sorted by download count (descending)
     * Format: [{filename, filetitle, coursecode, description, rating, download-number}, ...]
     */
    public List<Map<String, Object>> getTrendingFiles() {
        // Get all files from database
        List<MFile> files = mFileRepository.findAll();

        // Sort by download count (highest first) and take top 10
        return files.stream()
                .sorted((a, b) -> Integer.compare(
                        b.getDownloadNumber() != null ? b.getDownloadNumber() : 0,
                        a.getDownloadNumber() != null ? a.getDownloadNumber() : 0
                ))
                .limit(10)
                .map(f -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("filename", f.getFilename());
                    map.put("filetitle", f.getFiletitle());
                    map.put("coursecode", f.getCoursecode());
                    map.put("description", f.getDescription());
                    map.put("rating", f.getRating());
                    map.put("download-number", f.getDownloadNumber());
                    return map;
                })
                .collect(Collectors.toList());
    }
}