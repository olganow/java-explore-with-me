package ru.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewCategoryDto {

    @NotBlank(message = "Name can't be blank")
    @Size(max = 32)
    String name;
}
