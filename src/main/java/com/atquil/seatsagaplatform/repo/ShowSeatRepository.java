package com.atquil.seatsagaplatform.repo;

import com.atquil.seatsagaplatform.constants.SeatStatus;
import com.atquil.seatsagaplatform.entity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author atquil
 */
@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    // Fetch specific seats for a specific show
    // We explicitly check showId to ensure someone doesn't send a seatId from a different show
    @Query("SELECT ss FROM ShowSeat ss JOIN FETCH ss.seat WHERE ss.show.id = :showId AND ss.seat.id IN :seatIds")
    List<ShowSeat> findSeatsForBooking(@Param("showId") Long showId, @Param("seatIds") List<Long> seatIds);

    List<ShowSeat> findByShowId(Long showId);

    //For Actuator
    // Count by status
    long countByStatus(SeatStatus status);

    // For dashboard metrics
    @Query("SELECT s.status as status, COUNT(s) as count FROM ShowSeat s GROUP BY s.status")
    List<Object[]> getSeatStatusDistribution();

    // Recent activity
    @Query("SELECT COUNT(s) FROM ShowSeat s WHERE s.status = 'BOOKED' AND s.createdAt <= :since")
    long countRecentBookings(LocalDateTime since);
}
