package com.umerqureshicodes.backend.controller;

import com.umerqureshicodes.backend.service.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Handles admin operations (course codes, file deletion)
 * Maps to: getCourseCodeFromAdmins.php, deleteFileFromMFiles.php,
 *          deleteFileFromDownloadedFiles.php, deleteFileFromRatings.php,
 *          deleteFileFromUploads.php
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    /**
     * Get course codes associated with admin
     * Called by: admin.js & adminupload.js - getCourseCodeFromAdmins fetch
     * Returns: {success: boolean, message: string, courseCodes: []}
     */
    @PostMapping("/course-codes")
    public ResponseEntity<Map<String, Object>> getCourseCodes(HttpSession session) {
        String username = (String) session.getAttribute("username");
        Boolean isAdmin = (Boolean) session.getAttribute("is_admin");

        if (username == null || !Boolean.TRUE.equals(isAdmin)) {
            return ResponseEntity.status(401).body(Map.of("success", false));
        }

        List<String> codes = adminService.getCourseCodes(username);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully retrieved coursecode from admin",
                "courseCodes", codes
        ));
    }

    /**
     * Delete file from mfiles table
     * Called by: deleteFileHandler.js - first delete step
     * Expects: filename
     * Returns: {success: boolean, message: string}
     */
    @PostMapping("/delete/mfiles")
    public ResponseEntity<Map<String, Object>> deleteFromMFiles(
            @RequestParam String filename,
            HttpSession session) {

        Boolean isAdmin = (Boolean) session.getAttribute("is_admin");
        if (!Boolean.TRUE.equals(isAdmin)) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Unauthorized"
            ));
        }

        boolean success = adminService.deleteFromMFiles(filename);
        return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? filename + " successfully deleted" : "File could not be deleted"
        ));
    }

    /**
     * Delete file from downloadedfiles table
     * Called by: deleteFileHandler.js - second delete step
     * Expects: filename
     * Returns: {success: boolean, message: string}
     */
    @PostMapping("/delete/downloadedfiles")
    public ResponseEntity<Map<String, Object>> deleteFromDownloadedFiles(
            @RequestParam String filename,
            HttpSession session) {

        Boolean isAdmin = (Boolean) session.getAttribute("is_admin");
        if (!Boolean.TRUE.equals(isAdmin)) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Unauthorized"
            ));
        }

        boolean success = adminService.deleteFromDownloadedFiles(filename);
        return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? filename + " successfully deleted from downloadedfiles" : "Failed"
        ));
    }

    /**
     * Delete file from ratings table
     * Called by: deleteFileHandler.js - third delete step
     * Expects: filename
     * Returns: {success: boolean, message: string}
     */
    @PostMapping("/delete/ratings")
    public ResponseEntity<Map<String, Object>> deleteFromRatings(
            @RequestParam String filename,
            HttpSession session) {

        Boolean isAdmin = (Boolean) session.getAttribute("is_admin");
        if (!Boolean.TRUE.equals(isAdmin)) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Unauthorized"
            ));
        }

        boolean success = adminService.deleteFromRatings(filename);
        return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? filename + " successfully deleted from ratings" : "Failed"
        ));
    }

    /**
     * Delete file from uploads folder
     * Called by: deleteFileHandler.js - fourth/final delete step
     * Expects: filename
     * Returns: {success: boolean, message: string}
     */
    @PostMapping("/delete/uploads")
    public ResponseEntity<Map<String, Object>> deleteFromUploads(
            @RequestParam String filename,
            HttpSession session) {

        Boolean isAdmin = (Boolean) session.getAttribute("is_admin");
        if (!Boolean.TRUE.equals(isAdmin)) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Unauthorized"
            ));
        }

        boolean success = adminService.deleteFromUploads(filename);
        return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "File deleted successfully" : "Could not delete file from uploads folder"
        ));
    }
}