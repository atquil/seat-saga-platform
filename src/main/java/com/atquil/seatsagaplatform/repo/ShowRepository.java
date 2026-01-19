package com.atquil.seatsagaplatform.repo;

import com.atquil.seatsagaplatform.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author atquil
 */
@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    @Query("""
        SELECT s FROM Show s
        JOIN FETCH s.movie m
        JOIN FETCH s.screen sc
        JOIN FETCH sc.theatre t
        WHERE t.city = :city
        AND m.id = :movieId
        AND s.startTime BETWEEN :startOfDay AND :endOfDay
        ORDER BY t.name, s.startTime
    """)
    List<Show> findShowsByCityAndMovieAndDate(
            @Param("city") String city,
            @Param("movieId") Long movieId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("""
        SELECT s FROM Show s
        JOIN FETCH s.movie
        JOIN FETCH s.screen sc
        JOIN FETCH sc.theatre
        WHERE s.id = :id
    """)
    Optional<Show> findByIdWithDetails(@Param("id") Long id);

    List<Show> findByScreenId(Long screenId);

    @Query("SELECT s FROM Show s JOIN s.screen sc WHERE sc.theatre.id = :theatreId")
    List<Show> findByScreenTheatreId(@Param("theatreId") Long theatreId);
}
