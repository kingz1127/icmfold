package com.example.InnerCityBackend.model.entity;

import com.example.InnerCityBackend.model.enums.ApprovalStatus;
import com.example.InnerCityBackend.model.enums.OutreachStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outreaches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outreach extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "full_address", nullable = false)
    private String fullAddress;

    private String continent;
    private String country;
    private String state;
    private String city;

    private Double latitude;
    private Double longitude;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OutreachStatus status = OutreachStatus.UPCOMING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Builder.Default
    private Integer beneficiariesCount = 0;

    @Builder.Default
    private Integer volunteersCount = 0;

    @Column(name = "created_by")
    private String createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    // DELETED: Manual empty methods to let Lombok work correctly
}