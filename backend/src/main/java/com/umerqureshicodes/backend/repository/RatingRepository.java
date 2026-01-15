package com.umerqureshicodes.backend.repository;

import com.umerqureshicodes.backend.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Rating entity
 * Provides database operations for ratings table
 * Tracks user ratings for files
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    /**
     * Check if a user has rated a specific file
     */
    Optional<Rating> findByMacIDAndFilename(String macID, String filename);

    /**
     * Count how many times a user has rated a file
     * Used to check if user can rate a file
     */
    int countByMacIDAndFilename(String macID, String filename);

    /**
     * Get all ratings for a specific file
     * Used when deleting a file
     */
    List<Rating> findByFilename(String filename);

    /**
     * Calculate average rating for a file
     * Used to update file's rating in mfiles table
     */
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.filename = ?1")
    Double getAverageRatingByFilename(String filename);
}
