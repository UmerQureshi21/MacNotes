package com.umerqureshicodes.backend.service;

import com.umerqureshicodes.backend.entity.*;
import com.umerqureshicodes.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Handles all file-related operations
 * Mirrors: upload.php, download.php, filePreview.php, getAverageRating.php,
 *          getUserFromFileName.php, ratingHistory.php, updateRating.php
 */
@Service
@RequiredArgsConstructor
public class FileService {
    private final MFileRepository mFileRepository;
    private final DownloadedFileRepository downloadedFileRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final RatingRepository ratingRepository;

    private static final String UPLOADS_DIR = "uploads/";

    /**
     * Get file preview data (download count)
     * Mirrors: filePreview.php
     */
    public Map<String, Object> getFilePreview(String filename) {
        Optional<MFile> fileOpt = mFileRepository.findByFilename(filename);
        Map<String, Object> response = new HashMap<>();

        int downloadCount = fileOpt
                .map(f -> f.getDownloadNumber() != null ? f.getDownloadNumber() : 0)
                .orElse(0);

        response.put("download_count", downloadCount);
        return response;
    }

    /**
     * Get file's average rating
     * Mirrors: getAverageRating.php
     */
    public Map<String, Object> getAverageRating(String filename) {
        Optional<MFile> fileOpt = mFileRepository.findByFilename(filename);
        Map<String, Object> response = new HashMap<>();

        double rating = fileOpt
                .map(f -> f.getRating() != null ? f.getRating() : 0.0)
                .orElse(0.0);

        response.put("rating", rating);
        return response;
    }

    /**
     * Get uploader username from filename
     * Mirrors: getUserFromFileName.php
     */
    public Map<String, Object> getUserFromFileName(String filename) {
        Optional<MFile> fileOpt = mFileRepository.findByFilename(filename);
        Map<String, Object> response = new HashMap<>();

        if (fileOpt.isPresent()) {
            String macID = fileOpt.get().getMacID();

            // Try to find in users table first
            Optional<User> userOpt = userRepository.findByMacID(macID);
            if (userOpt.isPresent()) {
                response.put("user_name", userOpt.get().getUsername());
                return response;
            }

            // Otherwise try admins table
            Optional<Admin> adminOpt = adminRepository.findByMacID(macID);
            if (adminOpt.isPresent()) {
                response.put("user_name", adminOpt.get().getUsername());
                return response;
            }
        }

        response.put("user_name", "");
        return response;
    }

    /**
     * Record a download
     * Mirrors: download.php
     * Updates:
     * - mfiles: increment download-number
     * - users: increment num_downloads
     * - downloadedfiles: insert new record
     */
    public void recordDownload(String username, String filename) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<MFile> fileOpt = mFileRepository.findByFilename(filename);

        if (userOpt.isEmpty() || fileOpt.isEmpty()) {
            throw new RuntimeException("User or file not found");
        }

        User user = userOpt.get();
        MFile file = fileOpt.get();

        // Update file download count
        file.setDownloadNumber((file.getDownloadNumber() != null ? file.getDownloadNumber() : 0) + 1);
        mFileRepository.save(file);

        // Update user download count
        user.setNumDownloads((user.getNumDownloads() != null ? user.getNumDownloads() : 0) + 1);
        userRepository.save(user);

        // Record in downloadedfiles table
        DownloadedFile dlFile = new DownloadedFile();
        dlFile.setFilename(filename);
        dlFile.setMacIDofDownloader(user.getMacID());
        dlFile.setMacIDofUploader(file.getMacID());
        dlFile.setFiletitle(file.getFiletitle());
        dlFile.setCoursecode(file.getCoursecode());
        dlFile.setDescription(file.getDescription());
        dlFile.setDownloadNumber(file.getDownloadNumber());
        dlFile.setRating(file.getRating());
        dlFile.setUploadTime(file.getUploadTime());
        downloadedFileRepository.save(dlFile);
    }

    /**
     * Check if user has already rated a file
     * Mirrors: ratingHistory.php
     */
    public boolean hasUserRated(String username, String filename) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }

        return ratingRepository.countByMacIDAndFilename(
                userOpt.get().getMacID(),
                filename
        ) > 0;
    }

    /**
     * Upload a file
     * Mirrors: upload.php
     * Steps:
     * 1. Save file to disk (uploads/ directory)
     * 2. Save metadata to mfiles table
     * 3. Increment user's upload count
     */
    public Map<String, Object> uploadFile(
            MultipartFile file,
            String filetitle,
            String coursecode,
            String description,
            String username) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a file to upload.");
                return response;
            }

            // Get user
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }

            // Create unique filename
            String originalFilename = file.getOriginalFilename();
            String filename = System.currentTimeMillis() + "_" + originalFilename;

            // Create uploads directory if it doesn't exist
            Path uploadsPath = Paths.get(UPLOADS_DIR);
            Files.createDirectories(uploadsPath);

            // Save file to disk
            Files.write(uploadsPath.resolve(filename), file.getBytes());

            // Save metadata to database
            User user = userOpt.get();
            MFile mFile = new MFile();
            mFile.setFilename(filename);
            mFile.setMacID(user.getMacID());
            mFile.setFiletitle(filetitle);
            mFile.setCoursecode(coursecode);
            mFile.setDescription(description);
            mFile.setFilesize(file.getSize());
            mFile.setDownloadNumber(0);
            mFile.setRating(0.0);
            mFile.setUploadTime(System.currentTimeMillis());
            mFileRepository.save(mFile);

            // Increment user's upload count
            user.setNumberUploads((user.getNumberUploads() != null ? user.getNumberUploads() : 0) + 1);
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "File uploaded successfully!");

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Error uploading file: " + e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Something went wrong. Try again!");
        }

        return response;
    }

    /**
     * Submit a rating for a file
     * Mirrors: updateRating.php
     * Steps:
     * 1. Insert rating into ratings table
     * 2. Calculate new average rating
     * 3. Update file's rating in mfiles table
     */
    public String submitRating(String username, String filename, Integer ratingValue) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        Optional<MFile> fileOpt = mFileRepository.findByFilename(filename);

        if (userOpt.isEmpty() || fileOpt.isEmpty()) {
            return "User or file not found";
        }

        User user = userOpt.get();
        MFile file = fileOpt.get();

        try {
            // Insert rating
            Rating rating = new Rating();
            rating.setMacID(user.getMacID());
            rating.setFilename(filename);
            rating.setRating(ratingValue);
            ratingRepository.save(rating);

            // Calculate new average rating
            Double avgRating = ratingRepository.getAverageRatingByFilename(filename);
            if (avgRating != null) {
                // Round to 1 decimal place
                file.setRating(Math.round(avgRating * 10.0) / 10.0);
            } else {
                file.setRating(0.0);
            }
            mFileRepository.save(file);

            return "Rating submitted successfully!";
        } catch (Exception e) {
            return "Error submitting rating: " + e.getMessage();
        }
    }
}