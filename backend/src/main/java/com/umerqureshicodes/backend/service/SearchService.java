package com.umerqureshicodes.backend.service;

import com.umerqureshicodes.backend.entity.DownloadedFile;
import com.umerqureshicodes.backend.entity.MFile;
import com.umerqureshicodes.backend.entity.User;
import com.umerqureshicodes.backend.repository.DownloadedFileRepository;
import com.umerqureshicodes.backend.repository.MFileRepository;
import com.umerqureshicodes.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles search operations
 * Mirrors: search.php, myfiles.php
 */
@Service
@RequiredArgsConstructor
public class SearchService {
    private final MFileRepository mFileRepository;
    private final DownloadedFileRepository downloadedFileRepository;
    private final UserRepository userRepository;

    /**
     * Search all files (general search)
     * Mirrors: search.php logic
     * Supports filtering by:
     * - File title (keyword)
     * - Course codes (multiple)
     * - File size range
     * - Sorting by download-number, rating, upload_time, or filetitle
     */
    public List<Map<String, Object>> searchFiles(
            String filetitle,
            List<String> coursecodes,
            Long minFilesize,
            Long maxFilesize,
            String orderBy) {

        List<MFile> results;

        // Search with size filter if provided
        if (minFilesize != null && maxFilesize != null) {
            results = mFileRepository.searchByTitleSizeAndCourse(
                    filetitle, minFilesize, maxFilesize, coursecodes);
        } else {
            // Search without size filter
            results = mFileRepository.searchByCourseAndTitle(filetitle, coursecodes);
        }

        // Sort results
        sortResults(results, orderBy);

        // Convert to map format for response
        return convertToMap(results);
    }

    /**
     * Search user's uploaded files
     * Mirrors: myfiles.php logic for "uploads" category
     */
    public List<Map<String, Object>> searchMyUploads(
            String macID,
            String filetitle,
            List<String> coursecodes,
            String orderBy) {

        // Get all files uploaded by this user matching the title
        List<MFile> results = mFileRepository.findByMacIDAndFiletitleContaining(macID, filetitle);

        // Filter by coursecodes if any are selected
        if (!coursecodes.isEmpty()) {
            results = results.stream()
                    .filter(f -> coursecodes.contains(f.getCoursecode()))
                    .collect(Collectors.toList());
        }

        // Sort results
        sortResults(results, orderBy);

        return convertToMap(results);
    }

    /**
     * Search user's downloaded files
     * Mirrors: myfiles.php logic for "downloads" category
     */
    public List<Map<String, Object>> searchMyDownloads(
            String macID,
            String filetitle,
            List<String> coursecodes,
            String orderBy) {

        // Get all files downloaded by this user matching the title
        List<DownloadedFile> results = downloadedFileRepository
                .findByFiletitleContainingAndMacIDofDownloader(filetitle, macID);

        // Filter by coursecodes if any are selected
        if (!coursecodes.isEmpty()) {
            results = results.stream()
                    .filter(f -> coursecodes.contains(f.getCoursecode()))
                    .collect(Collectors.toList());
        }

        // Sort results
        sortDownloadResults(results, orderBy);

        return convertDownloadedToMap(results);
    }

    /**
     * Get user's macID
     * Mirrors: myfiles.php logic for "macID" category
     * Used by myfiles.js to get the current user's macID
     */
    public List<Map<String, Object>> getMacID(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("macID", userOpt.get().getMacID());
        return List.of(map);
    }

    /**
     * Sort MFile results by various options
     * orderBy can be:
     * - "filetitle" - alphabetically A-Z
     * - "rating" - highest rated first
     * - "upload_time" - newest first
     * - "download-number" or anything else - most downloaded first (default)
     */
    private void sortResults(List<MFile> results, String orderBy) {
        if ("filetitle".equals(orderBy)) {
            // Sort alphabetically by file title
            results.sort(Comparator.comparing(MFile::getFiletitle));
        } else if ("rating".equals(orderBy)) {
            // Sort by highest rating first
            results.sort((a, b) -> Double.compare(
                    b.getRating() != null ? b.getRating() : 0.0,
                    a.getRating() != null ? a.getRating() : 0.0
            ));
        } else if ("upload_time".equals(orderBy)) {
            // Sort by newest first
            results.sort((a, b) -> Long.compare(
                    b.getUploadTime() != null ? b.getUploadTime() : 0L,
                    a.getUploadTime() != null ? a.getUploadTime() : 0L
            ));
        } else {
            // Default: sort by most downloaded first
            results.sort((a, b) -> Integer.compare(
                    b.getDownloadNumber() != null ? b.getDownloadNumber() : 0,
                    a.getDownloadNumber() != null ? a.getDownloadNumber() : 0
            ));
        }
    }

    /**
     * Sort DownloadedFile results by various options
     * Same sorting logic as sortResults
     */
    private void sortDownloadResults(List<DownloadedFile> results, String orderBy) {
        if ("filetitle".equals(orderBy)) {
            results.sort(Comparator.comparing(DownloadedFile::getFiletitle));
        } else if ("rating".equals(orderBy)) {
            results.sort((a, b) -> Double.compare(
                    b.getRating() != null ? b.getRating() : 0.0,
                    a.getRating() != null ? a.getRating() : 0.0
            ));
        } else if ("upload_time".equals(orderBy)) {
            results.sort((a, b) -> Long.compare(
                    b.getUploadTime() != null ? b.getUploadTime() : 0L,
                    a.getUploadTime() != null ? a.getUploadTime() : 0L
            ));
        } else {
            results.sort((a, b) -> Integer.compare(
                    b.getDownloadNumber() != null ? b.getDownloadNumber() : 0,
                    a.getDownloadNumber() != null ? a.getDownloadNumber() : 0
            ));
        }
    }

    /**
     * Convert MFile list to Map list for JSON response
     * Includes: filename, filetitle, coursecode, description, rating, download-number, filesize
     */
    private List<Map<String, Object>> convertToMap(List<MFile> files) {
        return files.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("filename", f.getFilename());
            map.put("filetitle", f.getFiletitle());
            map.put("coursecode", f.getCoursecode());
            map.put("description", f.getDescription());
            map.put("rating", f.getRating());
            map.put("download-number", f.getDownloadNumber());
            map.put("filesize", f.getFilesize());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * Convert DownloadedFile list to Map list for JSON response
     * Includes: filename, filetitle, coursecode, description, rating, download-number
     */
    private List<Map<String, Object>> convertDownloadedToMap(List<DownloadedFile> files) {
        return files.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("filename", f.getFilename());
            map.put("filetitle", f.getFiletitle());
            map.put("coursecode", f.getCoursecode());
            map.put("description", f.getDescription());
            map.put("rating", f.getRating());
            map.put("download-number", f.getDownloadNumber());
            return map;
        }).collect(Collectors.toList());
    }
}