package com.umerqureshicodes.backend.controller;


import com.umerqureshicodes.backend.service.FileService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

/**
 * Handles file operations (upload, download, rating, preview)
 * Maps to: upload.php, download.php, filePreview.php, getAverageRating.php,
 *          getUserFromFileName.php, ratingHistory.php, updateRating.php
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    /**
     * Upload a file
     * Called by: upload.js & adminupload.js - uploadButton click
     * Expects: file (MultipartFile), filetitle, coursecode, description
     * Returns: {success: boolean, message: string}
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam String filetitle,
            @RequestParam String coursecode,
            @RequestParam String description,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "No active session"
            ));
        }

        Map<String, Object> response = fileService.uploadFile(
                file, filetitle, coursecode, description, username);
        return ResponseEntity.ok(response);
    }

    /**
     * Get file preview data (download count)
     * Called by: searchfiledetails.js & adminsearchfiledetails.js - setInterval
     * Expects: filename
     * Returns: {download_count: number}
     */
    @PostMapping("/preview")
    public ResponseEntity<Map<String, Object>> getFilePreview(@RequestParam String filename) {
        Map<String, Object> response = fileService.getFilePreview(filename);
        return ResponseEntity.ok(response);
    }

    /**
     * Get file's average rating
     * Called by: searchfiledetails.js & adminsearchfiledetails.js - setInterval
     * Expects: filename
     * Returns: {rating: double}
     */
    @PostMapping("/rating")
    public ResponseEntity<Map<String, Object>> getAverageRating(@RequestParam String filename) {
        Map<String, Object> response = fileService.getAverageRating(filename);
        return ResponseEntity.ok(response);
    }

    /**
     * Get uploader username from filename
     * Called by: searchfiledetails.js & adminsearchfiledetails.js - window load
     * Expects: filename
     * Returns: {user_name: string}
     */
    @PostMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserFromFileName(@RequestParam String filename) {
        Map<String, Object> response = fileService.getUserFromFileName(filename);
        return ResponseEntity.ok(response);
    }

    /**
     * Record a download
     * Called by: search.js & adminsearch.js - download button click
     * Expects: filename
     * Updates: download count in db, adds to downloadedfiles table
     * Returns: {message: string}
     */
    @PostMapping("/download")
    public ResponseEntity<Map<String, String>> recordDownload(
            @RequestParam String filename,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No active session"));
        }

        fileService.recordDownload(username, filename);
        return ResponseEntity.ok(Map.of("message", "Download recorded"));
    }

    /**
     * Check if user has already rated a file
     * Called by: searchfiledetails.js & adminsearchfiledetails.js - window load
     * Expects: filename
     * Returns: {rated: boolean}
     */
    @PostMapping("/rating-history")
    public ResponseEntity<Map<String, Boolean>> checkRatingHistory(
            @RequestParam String filename,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", false));
        }

        boolean hasRated = fileService.hasUserRated(username, filename);
        return ResponseEntity.ok(Map.of("rated", hasRated));
    }

    /**
     * Submit a rating for a file
     * Called by: searchfiledetails.js & adminsearchfiledetails.js - submit-rating-btn click
     * Expects: rating (1-5), filename
     * Returns: response text message
     */
    @PostMapping("/rating-submit")
    public ResponseEntity<String> submitRating(
            @RequestParam Integer rating,
            @RequestParam String filename,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body("No active session");
        }

        String result = fileService.submitRating(username, filename, rating);
        return ResponseEntity.ok(result);
    }
}