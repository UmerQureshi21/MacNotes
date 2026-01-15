package com.umerqureshicodes.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Admin entity
 * Maps to: admins table
 */
@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    @Column(name = "macID")
    private String macID;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "course_code")
    private String courseCode;
}