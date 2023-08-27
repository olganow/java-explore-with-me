package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StatsClient {

    private String baseUrl = "http://localhost:9090";
    private final WebClient client;

    public StatsClient() {
        this.client = WebClient.create(baseUrl);
    }

    public void saveStats(String app, String uri, String ip, LocalDateTime timestamp) {
        final StatsDto endpointHit = new StatsDto(app, uri, ip, timestamp);
        log.info("Save stats {}", endpointHit);
        this.client.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHit, StatsDto.class)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public ResponseEntity<List<ViewStatsDto>> getStats(String start, String end, String[] uris, Boolean isUnique) {
        log.info("Get stats with start date {}, end date {}, uris {}, unique {}", start, end, uris, isUnique);
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", isUnique)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(ViewStatsDto.class)
                .block();
    }
}