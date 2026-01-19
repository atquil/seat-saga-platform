package com.atquil.seatsagaplatform.repo;

import com.atquil.seatsagaplatform.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author atquil
 */

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    List<Partner> id(Long id);
}
