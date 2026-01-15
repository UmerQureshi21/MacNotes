package com.umerqureshicodes.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * MFile entity
 * Maps to: mfiles table
 * Represents uploaded files
 */
@Entity
@Table(name = "mfiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MFile {
    @Id
    @Column(name = "filename")
    private String filename;

    @Column(name = "macID", nullable = false)
    private String macID;

    @Column(name = "filetitle", nullable = false)
    private String filetitle;

    @Column(name = "coursecode", nullable = false)
    private String coursecode;

    @Column(name = "description")
    private String description;

    @Column(name = "filesize")
    private Long filesize;

    @Column(name = "`download-number`", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer downloadNumber = 0;

    @Column(name = "rating", nullable = false, columnDefinition = "DECIMAL(3,1) DEFAULT 0")
    private Double rating = 0.0;

    @Column(name = "upload_time")
    private Long uploadTime;
}