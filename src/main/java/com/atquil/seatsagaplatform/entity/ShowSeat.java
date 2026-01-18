package com.atquil.seatsagaplatform.entity;

import com.atquil.seatsagaplatform.constants.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author atquil
 */
@Entity
@Table(name = "show_seat", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"show_id", "seat_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowSeat extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status; // e.g., AVAILABLE, LOCKED, BOOKED

    @Column(nullable = false)
    private BigDecimal price;

    @Version // Optimistic Locking
    private Integer version;
}
