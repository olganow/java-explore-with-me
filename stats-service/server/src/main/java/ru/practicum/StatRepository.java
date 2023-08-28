package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.StatHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<StatHit, Integer> {
    @Query("select new ru.practicum.dto.ViewStatsDto(s.app, s.uri, count(distinct s.ip))" +
            "from StatHit as s " +
            "where s.timestamp between :start and :end " +
            "group by s.app, s.uri " +
            "order by count(distinct(s.ip)) desc")

    List<ViewStatsDto> getStatsByUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.ViewStatsDto(s.app, s.uri, count(s.ip))" +
            "from StatHit as s " +
            "where s.timestamp between :start and :end " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewStatsDto> getAllStats(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.ViewStatsDto(s.app, s.uri, count(distinct s.ip))" +
            "from StatHit as s " +
            "where s.timestamp between :start and :end " +
            "and s.uri in :uris " +
            "group by s.app, s.uri " +
            "order by count(distinct(s.ip)) desc")
    List<ViewStatsDto> getStatsByUrisByUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.ViewStatsDto(s.app, s.uri, count(s.ip))" +
            "from StatHit as s " +
            "where s.timestamp between :start and :end " +
            "and s.uri in :uris " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewStatsDto> getAllStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}