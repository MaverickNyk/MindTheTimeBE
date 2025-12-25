package com.mindthetime.backend.controller;

import com.mindthetime.backend.model.ArrivalPrediction;
import com.mindthetime.backend.service.ArrivalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prediction")
@RequiredArgsConstructor
public class ArrivalsController {

    private final ArrivalsService arrivalsService;

    @GetMapping
    public List<ArrivalPrediction> getArrivals(
            @RequestParam String stationId,
            @RequestParam(required = false) String line,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) Integer limit) {

        return arrivalsService.getFilteredArrivals(stationId, line, direction, limit);
    }
}
