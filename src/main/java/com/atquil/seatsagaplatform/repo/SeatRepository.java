package com.atquil.seatsagaplatform.repo;

import com.atquil.seatsagaplatform.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author atquil
 */
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScreenId(Long screenId);
    long countByScreenId(Long screenId);
}
