package com.example.InnerCityBackend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "partners")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Partner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String partnerName;

    @Column(columnDefinition = "TEXT") // For storing Base64 image string
    private String partnerImage;
}