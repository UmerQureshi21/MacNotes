package com.umerqureshicodes.backend.repository;

import com.umerqureshicodes.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for User entity
 * Provides database operations for users table
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByMacID(String macID);
    Optional<User> findByUsername(String username);
}