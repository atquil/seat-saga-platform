package com.atquil.seatsagaplatform.service;

import com.atquil.seatsagaplatform.dto.PartnerRequest;
import com.atquil.seatsagaplatform.entity.Partner;
import com.atquil.seatsagaplatform.repo.PartnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author atquil
 */
@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;

    @Transactional(readOnly = true)
    public List<Partner> getAll() {
        return partnerRepository.findAll();
    }

    @Transactional
    public Partner add(PartnerRequest request) {
        Partner partner = Partner.builder()
                .name(request.name())
                .contactEmail(request.contactEmail())
                .apiKey(UUID.randomUUID().toString())  // Auto-generate API key
                .build();
        return partnerRepository.save(partner);
    }

    @Transactional
    public Partner update(Long id, PartnerRequest request) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found: " + id));

        partner.setName(request.name());
        partner.setContactEmail(request.contactEmail());

        return partnerRepository.save(partner);
    }

    public Partner getById(Long id) {
        return partnerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found: " + id));
    }

    public void delete(Long id) {
        partnerRepository.deleteById(id);
    }
}