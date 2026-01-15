package com.umerqureshicodes.backend.repository;

import com.umerqureshicodes.backend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Admin entity
 * Provides database operations for admins table
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {
    Optional<Admin> findByMacID(String macID);
    Optional<Admin> findByUsername(String username);
    List<Admin> findAllByUsername(String username);
}