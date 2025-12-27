package com.mindthetime.backend.controller;

import com.mindthetime.backend.model.LineStatusResponse;
import com.mindthetime.backend.service.LineStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/status")
@RequiredArgsConstructor
public class LineStatusController {

    private final LineStatusService lineStatusService;

    @GetMapping("/lines")
    public List<LineStatusResponse> getLineStatuses() {
        return lineStatusService.getLineStatusesFromFirebase();
    }
}
