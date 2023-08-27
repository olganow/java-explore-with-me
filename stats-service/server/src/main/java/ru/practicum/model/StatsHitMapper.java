package ru.practicum.model;

import ru.practicum.dto.StatsDto;

public class StatsHitMapper {


    public static StatHit StatsDtoToStats(StatsDto statsDto) {
        return StatHit.builder()
                .app(statsDto.getApp())
                .uri(statsDto.getUri())
                .ip(statsDto.getIp())
                .timestamp(statsDto.getTimestamp())
                .build();
    }

}