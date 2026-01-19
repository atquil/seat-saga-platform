package com.atquil.seatsagaplatform.repo;

import com.atquil.seatsagaplatform.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author atquil
 */
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    List<Screen> findByTheatreId(Long theatreId);
}
