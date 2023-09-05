package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.events.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.DATE_DEFAULT;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewEventDto {

    @NotBlank
    @Size(max = 128, min = 3)
    private String title;

    @NotBlank
    @Size(max = 512, min = 3)
    private String annotation;

    @NotNull(message = "Category can't be blank")
    private Long category;

    @NotBlank
    @Size(max = 2048, min = 3)
    private String description;

    @NotNull
    @Future
    @JsonFormat(pattern = DATE_DEFAULT)
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid = false;

    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

}
