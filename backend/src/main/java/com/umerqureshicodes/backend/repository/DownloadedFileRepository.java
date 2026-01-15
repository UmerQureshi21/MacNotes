package com.umerqureshicodes.backend.repository;


import com.umerqureshicodes.backend.entity.DownloadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for DownloadedFile entity
 * Provides database operations for downloadedfiles table
 * Tracks which users downloaded which files
 */
@Repository
public interface DownloadedFileRepository extends JpaRepository<DownloadedFile, Long> {
    /**
     * Find a downloaded file by its filename
     */
    Optional<DownloadedFile> findByFilename(String filename);

    /**
     * Get all files downloaded by a specific user
     */
    List<DownloadedFile> findByMacIDofDownloader(String macIDofDownloader);

    /**
     * Find files downloaded by user containing title keyword
     * Used by myfiles.js for "My Downloads" search
     */
    List<DownloadedFile> findByFiletitleContainingAndMacIDofDownloader(String filetitle, String macIDofDownloader);
}