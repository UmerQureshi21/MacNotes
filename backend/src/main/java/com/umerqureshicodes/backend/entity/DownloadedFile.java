package com.umerqureshicodes.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * DownloadedFile entity
 * Maps to: downloadedfiles table
 * Tracks downloads by users
 */
@Entity
@Table(name = "downloadedfiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "macIDofDownloader")
    private String macIDofDownloader;

    @Column(name = "macIDofUploader")
    private String macIDofUploader;

    @Column(name = "filetitle")
    private String filetitle;

    @Column(name = "coursecode")
    private String coursecode;

    @Column(name = "description")
    private String description;

    @Column(name = "`download-number`")
    private Integer downloadNumber;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "upload_time")
    private Long uploadTime;
}