package com.example.InnerCityBackend.model.entity;

import com.example.InnerCityBackend.model.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "volunteers")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Volunteer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fullName;
    private String email;
    private String kingsChatHandle;
    private String country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category preferredMissionField;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subcategory_id")
    private Subcategory preferredActivity;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;
}