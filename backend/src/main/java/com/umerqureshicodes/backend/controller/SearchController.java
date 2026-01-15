package com.umerqureshicodes.backend.controller;

import com.umerqureshicodes.backend.service.SearchService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles search operations for files
 * Maps to: search.php, myfiles.php
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    /**
     * Search all files
     * Called by: search.js & adminsearch.js - getNotes function
     * Expects: query, coursecodefilter (JSON), filesizefilter (JSON), orderbyoption
     * Returns: {error: string, result: []}
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> searchFiles(
            @RequestParam String query,
            @RequestParam(required = false) String coursecodefilter,
            @RequestParam(required = false) String filesizefilter,
            @RequestParam(required = false) String orderbyoption) {

        List<String> coursecodes = parseCoursecodes(coursecodefilter);

        // If no coursecodes selected, return empty
        if (coursecodes.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "error", "",
                    "result", Collections.emptyList()
            ));
        }

        Long minFilesize = null, maxFilesize = null;
        if (filesizefilter != null && !filesizefilter.isBlank()) {
            try {
                Map<String, Long> sizes = parseFilesizes(filesizefilter);
                minFilesize = sizes.get("min");
                maxFilesize = sizes.get("max");
            } catch (Exception e) {
                // Parsing error - continue without filesize filter
            }
        }

        String orderBy = orderbyoption != null ? orderbyoption : "download-number";
        var results = searchService.searchFiles(query, coursecodes, minFilesize, maxFilesize, orderBy);

        return ResponseEntity.ok(Map.of(
                "error", "",
                "result", results
        ));
    }

    /**
     * Search user's files (uploads/downloads) or get macID
     * Called by: myfiles.js - submitForm function
     * Expects: filetitle, macID, category, coursecodefilter, orderbyoption
     * category can be: "macID", "uploads", or "downloads"
     * Returns: {error: string, message: []}
     */
    @PostMapping("/myfiles")
    public ResponseEntity<Map<String, Object>> searchMyFiles(
            @RequestParam String filetitle,
            @RequestParam String macID,
            @RequestParam String category,
            @RequestParam(required = false) String coursecodefilter,
            @RequestParam(required = false) String orderbyoption,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No active session"));
        }

        List<String> coursecodes = parseCoursecodes(coursecodefilter);
        String orderBy = orderbyoption != null ? orderbyoption : "filetitle";

        // If category is "macID", return user's macID
        if ("macID".equals(category)) {
            List<Map<String, Object>> result = searchService.getMacID(username);
            return ResponseEntity.ok(Map.of(
                    "error", "",
                    "message", result
            ));
        }
        // If category is "uploads", search user's uploads
        else if ("uploads".equals(category)) {
            var results = searchService.searchMyUploads(macID, filetitle, coursecodes, orderBy);
            return ResponseEntity.ok(Map.of(
                    "error", "",
                    "message", results
            ));
        }
        // Otherwise search downloads
        else {
            var results = searchService.searchMyDownloads(macID, filetitle, coursecodes, orderBy);
            return ResponseEntity.ok(Map.of(
                    "error", "",
                    "message", results
            ));
        }
    }

    /**
     * Parse coursecodes from JSON string
     * Input format: {"coursecodes": ["1MD3", "1JC3"]}
     * Returns: List of coursecodes
     */
    private List<String> parseCoursecodes(String filter) {
        if (filter == null || filter.isBlank()) {
            return new java.util.ArrayList<>();
        }

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, List<String>> map = mapper.readValue(filter, Map.class);
            return map.getOrDefault("coursecodes", new java.util.ArrayList<>());
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Parse filesizes from JSON string
     * Input format: {"min": 0, "max": 5000}
     * Values are in KB, converted to bytes by multiplying by 1024
     * Returns: Map with "min" and "max" keys
     */
    private Map<String, Long> parseFilesizes(String filter) {
        Map<String, Long> sizes = new HashMap<>();

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Long> map = mapper.readValue(filter, Map.class);

            // Convert KB to bytes
            sizes.put("min", (map.getOrDefault("min", 0L)) * 1024);
            sizes.put("max", (map.getOrDefault("max", Long.MAX_VALUE / 1024)) * 1024);
        } catch (Exception e) {
            sizes.put("min", 0L);
            sizes.put("max", Long.MAX_VALUE);
        }

        return sizes;
    }
}