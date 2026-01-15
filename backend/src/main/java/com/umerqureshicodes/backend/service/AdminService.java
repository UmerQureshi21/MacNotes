package com.umerqureshicodes.backend.service;

import com.umerqureshicodes.backend.entity.Admin;
import com.umerqureshicodes.backend.entity.DownloadedFile;
import com.umerqureshicodes.backend.entity.MFile;
import com.umerqureshicodes.backend.entity.Rating;
import com.umerqureshicodes.backend.repository.AdminRepository;
import com.umerqureshicodes.backend.repository.DownloadedFileRepository;
import com.umerqureshicodes.backend.repository.MFileRepository;
import com.umerqureshicodes.backend.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles admin operations
 * Mirrors: getCourseCodeFromAdmins.php, deleteFileFromMFiles.php,
 *          deleteFileFromDownloadedFiles.php, deleteFileFromRatings.php,
 *          deleteFileFromUploads.php
 */
@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final MFileRepository mFileRepository;
    private final DownloadedFileRepository downloadedFileRepository;
    private final RatingRepository ratingRepository;

    private static final String UPLOADS_DIR = "uploads/";

    /**
     * Get course codes for an admin
     * Mirrors: getCourseCodeFromAdmins.php
     */
    public List<String> getCourseCodes(String username) {
        List<Admin> admins = adminRepository.findAllByUsername(username) ;
        return admins.stream()
                .map(Admin::getCourseCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Delete file from mfiles table
     * Mirrors: deleteFileFromMFiles.php
     */
    public boolean deleteFromMFiles(String filename) {
        Optional<MFile> fileOpt = mFileRepository.findByFilename(filename);
        if (fileOpt.isPresent()) {
            mFileRepository.delete(fileOpt.get());
            return true;
        }
        return false;
    }

    /**
     * Delete file from downloadedfiles table
     * Mirrors: deleteFileFromDownloadedFiles.php
     */
    public boolean deleteFromDownloadedFiles(String filename) {
        try {
            List<DownloadedFile> files = downloadedFileRepository.findByFilename(filename)
                    .stream()
                    .collect(Collectors.toList());

            if (!files.isEmpty()) {
                downloadedFileRepository.deleteAll(files);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Delete file from ratings table
     * Mirrors: deleteFileFromRatings.php
     */
    public boolean deleteFromRatings(String filename) {
        try {
            List<Rating> ratings = ratingRepository.findByFilename(filename);
            if (!ratings.isEmpty()) {
                ratingRepository.deleteAll(ratings);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Delete file from uploads directory
     * Mirrors: deleteFileFromUploads.php
     */
    public boolean deleteFromUploads(String filename) {
        try {
            Path filePath = Paths.get(UPLOADS_DIR, filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
}