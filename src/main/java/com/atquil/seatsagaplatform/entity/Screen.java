package com.atquil.seatsagaplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author atquil
 */
@Entity
@Table(name = "screen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    @JsonIgnore
    private Theatre theatre;

    @Column(nullable = false)
    private String name;

    @Column(name = "total_seats")
    private Integer totalSeats;

    // Requires jackson-databind on classpath for JSONB mapping
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "seat_layout_json")
    private String seatLayoutJson;

    @Builder.Default
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();
}
