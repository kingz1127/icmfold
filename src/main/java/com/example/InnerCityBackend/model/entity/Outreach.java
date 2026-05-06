package com.example.InnerCityBackend.model.entity;

import com.example.InnerCityBackend.model.enums.ApprovalStatus;
import com.example.InnerCityBackend.model.enums.OutreachStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outreach")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outreach extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String fullAddress;
    private String continent;
    private String country;
    private String state;
    private String city;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private OutreachStatus status;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    private Integer beneficiariesCount = 0;
    private Integer volunteersCount = 0;

    @Column(name = "created_by")
    private String createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;
}