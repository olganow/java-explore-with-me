package ru.practicum.events.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.EventUpdatedDto;
import ru.practicum.events.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

import static ru.practicum.util.Constants.PAGE_DEFAULT_FROM;
import static ru.practicum.util.Constants.PAGE_DEFAULT_SIZE;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(value = "userId") Long userId,
                                    @Valid @RequestBody NewEventDto eventDto) {
        log.info("Create event {} of user with id= {}", eventDto, userId);
        return eventService.createEvent(userId, eventDto);
    }


    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Collection<EventShortDto> getEventsByUserId(@PathVariable(value = "userId") Long userId,
                                                       @RequestParam(value = "from", defaultValue = PAGE_DEFAULT_FROM)
                                                       @PositiveOrZero Integer from,
                                                       @RequestParam(value = "size", defaultValue = PAGE_DEFAULT_SIZE)
                                                       @Positive Integer size) {
        log.info("Get events of user with id= {}", userId);
        return eventService.getAllEventsByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable(value = "userId") Long userId,
                                     @PathVariable(value = "eventId") Long eventId) {
        log.info("Get event with id= {} of user with id= {}", eventId, userId);
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "eventId") Long eventId,
                                    @Valid @RequestBody EventUpdatedDto eventDto) {
        log.info("Updating event {} with id= {} of user with id= {}", eventDto, eventId, userId);
        return eventService.updateEventById(userId, eventId, eventDto);
    }

}