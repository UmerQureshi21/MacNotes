package com.umerqureshicodes.backend.repository;

import com.umerqureshicodes.backend.entity.MFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for MFile entity
 * Provides database operations for mfiles table
 *
 * Includes custom queries for:
 * - Search by title and course codes
 * - Search by title, file size, and course codes
 */
@Repository
public interface MFileRepository extends JpaRepository<MFile, String> {
    /**
     * Find a single file by filename
     */
    Optional<MFile> findByFilename(String filename);

    /**
     * Get all files uploaded by a specific user
     */
    List<MFile> findByMacID(String macID);

    /**
     * Find files uploaded by user containing title keyword
     * Used by myfiles.js for "My Uploads" search
     */
    List<MFile> findByMacIDAndFiletitleContaining(String macID, String filetitle);

    /**
     * Search files by title and course codes
     * Used by search.js for general search
     */
    @Query("SELECT f FROM MFile f WHERE f.filetitle LIKE %?1% AND f.coursecode IN ?2")
    List<MFile> searchByCourseAndTitle(String filetitle, List<String> coursecodes);

    /**
     * Search files by title, file size range, and course codes
     * Used by search.js when file size filter is applied
     * Size parameters are in bytes
     */
    @Query("SELECT f FROM MFile f WHERE f.filetitle LIKE %?1% AND f.filesize BETWEEN ?2 AND ?3 AND f.coursecode IN ?4")
    List<MFile> searchByTitleSizeAndCourse(String filetitle, Long minSize, Long maxSize, List<String> coursecodes);
}