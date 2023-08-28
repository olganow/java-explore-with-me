package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatsHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exeption.ValidationException;
import ru.practicum.model.StatsHitMapper;
import ru.practicum.model.StatHit;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StatServiceImpl implements StatService {

    private final StatRepository repository;

    @Override
    public void saveStat(StatsHitDto dto) {
        StatHit statHit = repository.save(StatsHitMapper.statsHitDtoToStatHit(dto));
        log.info("Save stat {}", statHit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean isUnique) {
        if (start.isAfter(end)) {
            log.error("End time can't be before start time");
            throw new ValidationException("End time can't be before start time");
        }

        if (isUnique && uris != null) {
            log.error("Get all stats with isUnique {} when uris {} ", isUnique, uris);
            return repository.getStatsByUrisByUniqueIp(start, end, uris);
        } else if (isUnique) {
            log.error("Get all stats with isUnique {} ", isUnique);
            return repository.getStatsByUniqueIp(start, end);
        } else if (uris != null) {
            log.error("Get all stats with isUnique {} when uris {} ", isUnique, uris);
            return repository.getAllStatsByUris(start, end, uris);
        } else {
            log.error("Get all stats with isUnique {} ", isUnique);
            return repository.getAllStats(start, end);
        }

    }
}