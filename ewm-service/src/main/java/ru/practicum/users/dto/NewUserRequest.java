package ru.practicum.users.dto;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
public class NewUserRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank(message = "Name can't be blank")
    private String name;
}