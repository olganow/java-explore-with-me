package ru.practicum.model;

import ru.practicum.dto.StatsHitDto;

public class StatsHitMapper {

    public static StatHit StatsDtoToStatHit(StatsHitDto statsDto) {
        return StatHit.builder()
                .app(statsDto.getApp())
                .uri(statsDto.getUri())
                .ip(statsDto.getIp())
                .timestamp(statsDto.getTimestamp())
                .build();
    }

}