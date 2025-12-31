package com.mindthetime.backend.controller;

import com.mindthetime.backend.model.ErrorResponse;
import com.mindthetime.backend.model.Station;
import com.mindthetime.backend.service.CacheRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/predictions")
@RequiredArgsConstructor
@Tag(name = "Predictions", description = "TFL Arrival Predictions")
public class ArrivalsController {

    private final CacheRetrievalService cacheRetrievalService;

    /**
     * Get predictions from Redis cache
     * 
     * @param station   Required. Comma-separated station IDs (e.g.,
     *                  "940GZZLUKSX,940GZZLUOXC")
     * @param mode      Required. Comma-separated mode/line IDs (e.g.,
     *                  "central,northern")
     * @param direction Required. Comma-separated directions (e.g.,
     *                  "inbound,outbound")
     * @return List of Station objects with nested line and direction data
     */
    @Operation(summary = "Get Arrival Predictions", description = "Retrieves arrival predictions for specified stations, modes, and directions from the Redis cache.")
    @ApiResponse(responseCode = "200", description = "Successful retrieval", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Station.class)))
    @ApiResponse(responseCode = "404", description = "Cache Miss - Data not yet available", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    public ResponseEntity<?> getPredictions(
            @Parameter(description = "Comma-separated station IDs (e.g. 940GZZLUKSX)", required = true) @RequestParam(required = true) String station,
            @Parameter(description = "Comma-separated transport modes or line IDs (e.g. northern,victoria)", required = true) @RequestParam(required = true) String mode,
            @Parameter(description = "Comma-separated directions (inbound,outbound)", required = true) @RequestParam(required = true) String direction) {

        // Retrieve from cache and structure as Station objects
        List<Station> stations = cacheRetrievalService.getStations(station, mode, direction);

        // If no data found in cache, return error
        if (stations.isEmpty()) {
            ErrorResponse error = ErrorResponse.builder()
                    .timestamp(java.time.LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error("Cache Miss")
                    .message("The middleware hasn't updated the arrivals yet. Please try again after some time.")
                    .path("/api/v1/predictions")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(stations);
    }
}
