package com.atquil.seatsagaplatform.repo;

import com.atquil.seatsagaplatform.entity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author atquil
 */
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    // Fetch specific seats for a specific show
    // We explicitly check showId to ensure someone doesn't send a seatId from a different show
    @Query("SELECT ss FROM ShowSeat ss JOIN FETCH ss.seat WHERE ss.show.id = :showId AND ss.seat.id IN :seatIds")
    List<ShowSeat> findSeatsForBooking(@Param("showId") Long showId, @Param("seatIds") List<Long> seatIds);

    List<ShowSeat> findByShowId(Long showId);
}
