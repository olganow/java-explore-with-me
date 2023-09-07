package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.util.enam.EventState;
import ru.practicum.util.enam.EventsSort;
import ru.practicum.util.Pagination;
import ru.practicum.StatsClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.util.enam.EventStateAction;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.handler.NotFoundException;
import ru.practicum.handler.ValidateException;
import ru.practicum.handler.ValidateDateException;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.util.enam.EventsSort.EVENT_DATE;
import static ru.practicum.util.enam.EventsSort.VIEWS;
import static ru.practicum.util.Constants.*;
import static ru.practicum.util.enam.EventState.*;
import static ru.practicum.events.dto.EventMapper.mapToEventFullDto;
import static ru.practicum.events.dto.EventMapper.mapToNewEvent;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAllEventsAdmin(List<Long> users,
                                                List<EventState> states,
                                                List<Long> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                Integer from,
                                                Integer size) {

        validDateParam(rangeStart, rangeEnd);
        PageRequest pageable = new Pagination(from, size, Sort.unsorted());
        List<Event> events = eventRepository.findAllForAdmin(users, states, categories, getRangeStart(rangeStart),
                pageable);

        log.info("Get all events in admin {}", events);
        return events.stream()
                .map(EventMapper::mapToEventFullDto)
                .collect(Collectors.toList());
    }


    @Override
    public EventFullDto updateEventByIdAdmin(Long eventId, EventUpdatedDto eventUpdatedDto) {
        Event event = getEventById(eventId);
        updateEventAdmin(event, eventUpdatedDto);
        event = eventRepository.save(event);

        log.info("Update event with id= {} in admin ", eventId);
        return mapToEventFullDto(event);
    }


    @Override
    public EventFullDto createEventPrivate(Long userId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " hasn't found"));
        Category category = getCategoryForEvent(newEventDto.getCategory());
        Event event = eventRepository.save(mapToNewEvent(newEventDto, user, category));
        log.info("User id= {} create event in admin", userId);
        return mapToEventFullDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAllEventsByUserIdPrivate(Long userId, int from, int size) {
        log.info("Get all events of user with id= {} in private", userId);
        return eventRepository.findAllWithInitiatorByInitiatorId(userId, new Pagination(from, size,
                        Sort.unsorted())).stream()
                .map(EventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByIdPrivate(Long userId, Long eventId) {
        Event event = getEventByIdAndInitiatorId(eventId, userId);
        log.info("Get event with id={} of user with id= {} in private", eventId, userId);
        return mapToEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByIdPrivate(Long userId, Long eventId, EventUpdatedDto eventUpdatedDto) {
        Event event = getEventByIdAndInitiatorId(eventId, userId);
        if (event.getState() == PUBLISHED || event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateException("Events with CANCELED or PENDING can be updated");
        }
        updateEvent(event, eventUpdatedDto);
        log.info("Update event with id={} of user with id= {} in private", eventId, userId);
        return mapToEventFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsPublic(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               EventsSort sort,
                                               Integer from,
                                               Integer size,
                                               HttpServletRequest request) {

        validDateParam(rangeStart, rangeEnd);
        Pagination pageable;
        final EventState state = PUBLISHED;
        List<Event> events;

        if (sort.equals(EVENT_DATE)) {
            pageable = new Pagination(from, size, Sort.by("eventDate"));
        } else {
            pageable = new Pagination(from, size, Sort.unsorted());
        }

        if (onlyAvailable) {
            events = eventRepository.findAllPublishStateNotAvailable(state, getRangeStart(rangeStart), categories,
                    paid, text, pageable);
        } else {
            events = eventRepository.findAllPublishStateAvailable(state, getRangeStart(rangeStart), categories,
                    paid, text, pageable);
        }

        if (rangeEnd != null) {
            events = getEventsBeforeRangeEnd(events, rangeEnd);
        }

        List<EventShortDto> result = events.stream()
                .map(EventMapper::mapToEventShortDto)
                .collect(Collectors.toList());

        saveViewInEvent(result);
        statsClient.saveStats(APP, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        if (sort.equals(VIEWS)) {
            return result.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }
        log.info("Get all  event with text {}, category {}", text, categories);
        return result;
    }

    @Override
    public EventFullDto getEventByIdPublic(Long id, HttpServletRequest request) {
        Event event = getEventById(id);
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Event with id=" + id + " hasn't not published");
        }
        EventFullDto fullDto = mapToEventFullDto(event);

        List<String> uris = List.of("/events/" + event.getId());
        List<ViewStatsDto> views = statsClient.getStats(START_DATE, END_DATE, uris, null).getBody();

        if (views != null) {
            fullDto.setViews(views.size());
        }
        statsClient.saveStats(APP, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        log.info("Get event with  id = {}}", id);
        return fullDto;
    }


    private Category getCategoryForEvent(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " hasn't found"));
    }

    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " hasn't found"));
    }

    private Event getEventByIdAndInitiatorId(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " hasn't found"));
    }

    private void updateEvent(Event event, EventUpdatedDto eventDto) {
        updateEventCommonFields(event, eventDto);

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(EventStateAction.CANCEL_REVIEW)) {
                event.setState(CANCELED);
            }
            if (eventDto.getStateAction().equals(EventStateAction.SEND_TO_REVIEW)) {
                event.setState(PENDING);
            }
        }
    }

    private void updateEventAdmin(Event event, EventUpdatedDto eventDto) {
        updateEventCommonFields(event, eventDto);

        if (eventDto.getStateAction() != null) {
            if (event.getState().equals(PENDING)) {
                if (eventDto.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
                    event.setState(CANCELED);
                }
                if (eventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                    event.setState(PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
            } else {
                throw new ValidateException("Cannot publish or cancel the event because it's not in the right state: "
                        + event.getState());
            }
        }

        if (eventDto.getEventDate() != null && event.getState().equals(PUBLISHED)) {
            if (eventDto.getEventDate().isAfter(event.getPublishedOn().plusHours(1))) {
                event.setEventDate(eventDto.getEventDate());
            } else {
                throw new ValidateDateException("The event date must be at least 1 hour after the published date.");
            }
        }
    }

    private void updateEventCommonFields(Event event, EventUpdatedDto eventDto) {
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getCategory() != null) {
            Category category = getCategoryForEvent(eventDto.getCategory());
            event.setCategory(category);
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getLocation() != null) {
            event.setLat(eventDto.getLocation().getLat());
            event.setLon(eventDto.getLocation().getLon());
        }
        if (eventDto.getEventDate() != null) {
            validateEventDate(eventDto.getEventDate());
            event.setEventDate(eventDto.getEventDate());
        }
    }

    private void validDateParam(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidateDateException("The range start date cannot be is after range end date");
            }
        }
    }

    private Long getId(String url) {
        String[] uri = url.split("/");
        return Long.valueOf(uri[uri.length - 1]);
    }

    private void saveViewInEvent(List<EventShortDto> result) {
        List<String> uris = result.stream()
                .map(eventShortDto -> "/events/" + eventShortDto.getId())
                .collect(Collectors.toList());

        List<ViewStatsDto> views = statsClient.getStats(START_DATE, END_DATE, uris, null).getBody();

        if (views != null) {
            Map<Long, Long> mapIdHits = views.stream()
                    .collect(Collectors.toMap(viewStats -> getId(viewStats.getUri()), ViewStatsDto::getHits));

            result.forEach(eventShortDto -> {
                Long eventId = eventShortDto.getId();
                Long viewsCount = mapIdHits.getOrDefault(eventId, 0L);
                eventShortDto.setViews(viewsCount);
            });
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {

        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateDateException("Event date should be after now");
        }
    }

    private LocalDateTime getRangeStart(LocalDateTime rangeStart) {
        if (rangeStart == null) {
            return LocalDateTime.now();
        }
        return rangeStart;
    }

    private List<Event> getEventsBeforeRangeEnd(List<Event> events, LocalDateTime rangeEnd) {
        return events.stream().filter(event -> event.getEventDate().isBefore(rangeEnd)).collect(Collectors.toList());
    }


}