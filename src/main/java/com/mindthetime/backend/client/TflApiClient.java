package com.mindthetime.backend.client;

import com.mindthetime.backend.model.ArrivalPrediction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Component
public class TflApiClient {

    private final WebClient webClient;

    @Value("${tfl.app.key}")
    private String appKey;

    public TflApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.tfl.gov.uk").build();
    }

    public List<ArrivalPrediction> getArrivals(String stationId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/StopPoint/{stationId}/Arrivals")
                        .queryParam("app_key", appKey)
                        .build(stationId))
                .retrieve()
                .bodyToFlux(ArrivalPrediction.class)
                .collectList()
                .block(); // Blocking here as our service layer is synchronous for now, could be made reactive later
    }
}
