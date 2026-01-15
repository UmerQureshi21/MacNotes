package com.umerqureshicodes.backend.entity;


import jakarta.persistence.*;
import lombok.*;

/**
 * User entity
 * Maps to: users table
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "macID")
    private String macID;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "number_uploads", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer numberUploads = 0;

    @Column(name = "num_downloads", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer numDownloads = 0;
}
