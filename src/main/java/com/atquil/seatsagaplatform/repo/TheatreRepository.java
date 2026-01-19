package com.atquil.seatsagaplatform.repo;

import com.atquil.seatsagaplatform.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author atquil
 */

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    List<Theatre> findByPartnerId(Long partnerId);

    @Query("SELECT DISTINCT t.city FROM Theatre t ORDER BY t.city")
    List<String> findDistinctCities();
}
