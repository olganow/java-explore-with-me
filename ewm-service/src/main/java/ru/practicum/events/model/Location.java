package ru.practicum.events.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Location {

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;

}