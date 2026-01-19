package com.atquil.seatsagaplatform.repo;

import com.atquil.seatsagaplatform.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author atquil
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
}
