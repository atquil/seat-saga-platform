package com.atquil.seatsagaplatform.controller;

import com.atquil.seatsagaplatform.dto.PartnerRequest;
import com.atquil.seatsagaplatform.entity.Partner;
import com.atquil.seatsagaplatform.service.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author atquil
 */
@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    @GetMapping
    @Operation(summary = "List all partners")
    public List<Partner> getAll() {
        return partnerService.getAll();
    }

    @PostMapping
    @Operation(summary = "Add new partner")
    public ResponseEntity<Partner> add(@Valid @RequestBody PartnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.add(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update partner")
    public Partner update(@PathVariable Long id, @Valid @RequestBody PartnerRequest request) {
        return partnerService.update(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get partner by ID")
    public Partner getById(@PathVariable Long id) {
        return partnerService.getById(id); // Ensure this exists in your service
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete partner")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        partnerService.delete(id); // Ensure this exists in your service
        return ResponseEntity.noContent().build();
    }
}
