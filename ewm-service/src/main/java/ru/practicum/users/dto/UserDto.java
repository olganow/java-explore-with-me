package ru.practicum.users.dto;

import lombok.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String email;
    private Long id;
    private String name;
}