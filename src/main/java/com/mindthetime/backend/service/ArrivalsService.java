package com.mindthetime.backend.service;

import com.mindthetime.backend.model.ArrivalPrediction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArrivalsService {

    private final TflDataService tflDataService;

    public List<ArrivalPrediction> getFilteredArrivals(String stationId, String lineId, String direction,
            Integer limit) {
        // Get full list from cached service
        List<ArrivalPrediction> allArrivals = tflDataService.getArrivalsForStation(stationId);

        Stream<ArrivalPrediction> stream = allArrivals.stream();

        if (lineId != null && !lineId.isEmpty()) {
            stream = stream
                    .filter(p -> lineId.equalsIgnoreCase(p.getLineName()) || lineId.equalsIgnoreCase(p.getLineName()));
            // Note: LineName in API usually matches.
        }

        if (direction != null && !direction.isEmpty()) {
            stream = stream.filter(
                    p -> direction.equalsIgnoreCase(p.getDirection()) || direction.equalsIgnoreCase(p.getTowards()));
        }

        stream = stream.sorted(Comparator.comparingInt(ArrivalPrediction::getTimeToStation));

        if (limit != null && limit > 0) {
            stream = stream.limit(limit);
        }

        return stream.collect(Collectors.toList());
    }
}
