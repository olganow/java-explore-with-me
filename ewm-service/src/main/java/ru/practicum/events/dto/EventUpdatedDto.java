package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.events.model.Location;
import ru.practicum.util.enam.StateActionEvent;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.DATE_DEFAULT;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdatedDto {

    @Size(max = 128, min = 3)
    private String title;

    @Size(max = 512, min = 3)
    private String annotation;

    @Size(max = 2048, min = 3)
    private String description;

    private Long category;

    @Future
    @JsonFormat(pattern = DATE_DEFAULT)
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private StateActionEvent stateAction;

}