package com.atquil.seatsagaplatform.repo;

import com.atquil.seatsagaplatform.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author atquil
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
}
