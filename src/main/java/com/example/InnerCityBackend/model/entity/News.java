package com.example.InnerCityBackend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "news")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class News extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 500)
    private String title;


    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT") // Important for storing Base64 data
    private String imageUrl;

    private String categoryId;

    private String continent;

    private String country;

    @Builder.Default
    private boolean isGlobal = false;

    private String createdBy;
}