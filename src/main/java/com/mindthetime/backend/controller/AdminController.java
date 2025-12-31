package com.mindthetime.backend.controller;

import com.mindthetime.backend.model.RefreshSummary;
import com.mindthetime.backend.model.LineStatusResponse;
import com.mindthetime.backend.service.FcmService;
import com.mindthetime.backend.service.LineStatusService;
import com.mindthetime.backend.service.RedisService;
import com.mindthetime.backend.service.TflPollingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Administrative operations for manual data refreshing and cleanup")
public class AdminController {

    private final RedisService redisService;
    private final FcmService fcmService;
    private final TflPollingService tflPollingService;
    private final LineStatusService lineStatusService;

    @Operation(summary = "Trigger Manual Refresh", description = "Manually triggers a data refresh for all configured transport modes from TFL API.")
    @ApiResponse(responseCode = "200", description = "Refresh completed successfully")
    @GetMapping("/refresh")
    public ResponseEntity<List<RefreshSummary>> refresh() {
        log.info("🔄 ADMIN: Manual refresh triggerred for all configured modes");
        List<RefreshSummary> summaries = tflPollingService.refreshAll();
        return ResponseEntity.ok(summaries);
    }

    @Operation(summary = "Trigger Line Status Refresh", description = "Manually triggers a refresh of line statuses from TFL API.")
    @ApiResponse(responseCode = "200", description = "Line statuses refreshed successfully")
    @GetMapping("/status/refresh")
    public ResponseEntity<List<LineStatusResponse>> refreshLineStatuses() {
        log.info("🔄 ADMIN: Manual line status refresh triggered");
        List<LineStatusResponse> statuses = lineStatusService.pollLineStatuses();
        return ResponseEntity.ok(statuses);
    }

    @Operation(summary = "System Cleanup", description = "Clears all data from Redis and sends a 'CLEAR' signal to FCM topics to reset client state.")
    @ApiResponse(responseCode = "200", description = "Cleanup completed")
    @GetMapping("/cleanup")
    public ResponseEntity<String> cleanup() {
        log.info("🔥 ADMIN: Cleanup requested. Clearing Redis and signaling FCM...");

        // 1. Find all active topics in Redis (both new and legacy)
        Set<String> keys = redisService.getKeys("*");
        if (keys != null && !keys.isEmpty()) {
            log.info("Found {} keys in Redis to signal...", keys.size());

            // Send CLEAR signal to relevant topics in parallel
            keys.parallelStream().forEach(key -> {
                // Topic name from Redis key
                // New format: Station_940GZZLUMGT -> Station_940GZZLUMGT
                // Legacy format: 940GZZLUMGT-Northern-Inbound -> 940GZZLUMGT-Northern-Inbound
                // (usually topic is normalized)
                if (key.startsWith("Station_") || (key.contains("-") && !key.contains(" "))) {
                    fcmService.sendClearSignal(key);
                }
            });
        }

        // 2. Perform nuclear flush of Redis
        redisService.flushAll();

        return ResponseEntity.ok("Cleanup completed successfully. Redis flushed and FCM signals sent.");
    }
}
