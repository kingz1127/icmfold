package com.example.InnerCityBackend.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "donations")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Donation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("kings_chat_full_name")
    private String kingsChatFullName;
    private String continent;

    private BigDecimal amount;
    private String currency; // NGN or USD

    @Column(unique = true, nullable = false)
    private String reference; // Unique transaction ID

    private String status; // PENDING, SUCCESS, FAILED, ABANDONED

    @JsonProperty("subcategory_id")
    private String subcategoryId; // Linked to the mission they chose
}