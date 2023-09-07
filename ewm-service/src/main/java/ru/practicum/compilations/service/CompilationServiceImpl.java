package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationUpdatedDto;
import ru.practicum.events.model.Event;
import ru.practicum.util.Pagination;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapper;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.handler.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.compilations.dto.CompilationMapper.mapToNewCompilation;
import static ru.practicum.compilations.dto.CompilationMapper.mapToCompilationDto;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;


    @Transactional
    @Override
    public CompilationDto createCompilationAdmin(NewCompilationDto compilationDto) {
        Compilation compilation = mapToNewCompilation(compilationDto);

        if (compilationDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(compilationDto.getEvents());
            compilation.setEvents(events);
        }
        log.info("Create compilation {} ", compilationDto);
        return mapToCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public CompilationDto updateCompilationByIdAdmin(Long compId, CompilationUpdatedDto compilationDto) {
        Compilation compilation = getCompilation(compId);
        Boolean pinned = compilationDto.getPinned();
        String title = compilationDto.getTitle();

        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(compilationDto.getEvents()));
        }
        if (pinned != null) {
            compilation.setPinned(pinned);
        }
        if (title != null) {
            compilation.setTitle(title);
        }
        log.info("Update compilation with id= {} ", compId);
        return mapToCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public void deleteCompilationByIdAdmin(Long compId) {
        getCompilation(compId);
        log.info("Delete compilation with id= {} ", compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAllCompilationsPublic(Boolean pinned, Integer from, Integer size) {
        log.info("Get all compilations");
        if (pinned == null) {
            return compilationRepository.findAll(new Pagination(from, size, Sort.unsorted())).getContent().stream()
                    .map(CompilationMapper::mapToCompilationDto)
                    .collect(Collectors.toList());
        }

        return compilationRepository.findAllByPinned(pinned, new Pagination(from, size, Sort.unsorted()))
                .getContent().stream()
                .map(CompilationMapper::mapToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationByIdPublic(Long id) {
        Compilation compilation = getCompilation(id);
        log.info("Get compilation with id= {} ", id);
        return mapToCompilationDto(compilation);
    }

    private Compilation getCompilation(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " hasn't found"));
    }
}