package com.atquil.seatsagaplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

/**
 * @author atquil
 */
@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    @Column(name = "google_sub", unique = true)
    private String googleSub;
}