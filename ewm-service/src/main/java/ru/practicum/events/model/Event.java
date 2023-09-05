package ru.practicum.events.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.category.model.Category;
import ru.practicum.util.enam.EventState;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.util.enam.EventState.PENDING;
import static ru.practicum.util.Constants.DATE_DEFAULT;

@Entity
@Table(name = "events")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(length = 128, nullable = false)
    private String title;

    @Column(length = 512, nullable = false)
    private String annotation;

    @Column(length = 2048, nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "event_date", nullable = false)
    @DateTimeFormat(pattern = DATE_DEFAULT)
    private LocalDateTime eventDate;

    @Column(name = "location_lat", nullable = false)
    private float lat;

    @Column(name = "location_lon", nullable = false)
    private float lon;

    @Column(nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit", nullable = false)

    private Integer participantLimit;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Enumerated(EnumType.STRING)
    @Column(length = 256, nullable = false)
    private EventState state = PENDING;

    @Column(name = "created_on", nullable = false)
    @DateTimeFormat(pattern = DATE_DEFAULT)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "published_on")
    @DateTimeFormat(pattern = DATE_DEFAULT)
    private LocalDateTime publishedOn;
}