package com.mindthetime.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrivalPrediction {
    private String id;
    private String stationName;
    private String lineName;
    private String platformName;
    private String direction;
    private String destinationName;
    private Integer timeToStation; // seconds
    private ZonedDateTime expectedArrival;
    private String currentLocation;
    private String towards;
}
