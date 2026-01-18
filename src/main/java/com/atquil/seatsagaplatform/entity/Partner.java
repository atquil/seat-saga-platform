package com.atquil.seatsagaplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author atquil
 */
@Entity
@Table(name = "seat_saga_partner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partner extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_email", nullable = false, unique = true)
    private String contactEmail;

    @Column(name = "api_key", nullable = false, unique = true)
    private String apiKey;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    // Relationships
    @Builder.Default
    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Theatre> theatres = new ArrayList<>();
}
