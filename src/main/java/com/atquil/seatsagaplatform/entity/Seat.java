package com.atquil.seatsagaplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author atquil
 */
@Entity
@Table(name = "seat", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"screen_id", "row_number", "seat_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    @JsonIgnore
    private Screen screen;

    @Column(name = "row_number", nullable = false, length = 5)
    private String rowNumber;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "seat_type")
    private String seatType; // e.g., REGULAR, PREMIUM, RECLINER
}
