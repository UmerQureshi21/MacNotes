package com.umerqureshicodes.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Rating entity
 * Maps to: ratings table
 * Tracks user ratings for files
 */
@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "macID")
    private String macID;

    @Column(name = "filename")
    private String filename;

    @Column(name = "rating")
    private Integer rating;
}