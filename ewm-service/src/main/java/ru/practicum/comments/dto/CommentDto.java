package ru.practicum.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.util.Constants.DATE_DEFAULT;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentDto {

    @Null
    private Long id;

    @Size(min = 2, max = 2000)
    @NotBlank(message = "Comment can't be blank")
    private String text;

    @Null
    private Long eventId;

    @Null
    private Long authorId;

    @Null
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_DEFAULT)
    private LocalDateTime created;
}

