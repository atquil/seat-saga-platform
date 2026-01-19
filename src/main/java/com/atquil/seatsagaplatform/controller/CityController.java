package com.atquil.seatsagaplatform.controller;

import com.atquil.seatsagaplatform.repo.TheatreRepository;
import com.atquil.seatsagaplatform.service.TheatreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author atquil
 */
@RestController
@RequiredArgsConstructor
public class CityController {

    private final TheatreService theatreService;

    @GetMapping("/api/cities")
    public List<String> getCities() {
        return theatreService.findDistinctCities();
    }
}
