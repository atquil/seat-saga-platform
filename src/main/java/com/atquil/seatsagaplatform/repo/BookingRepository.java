package com.atquil.seatsagaplatform.repo;

import com.atquil.seatsagaplatform.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author atquil
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    long countByCreatedAtAfter(LocalDateTime date);

}
