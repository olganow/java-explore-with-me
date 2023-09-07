package ru.practicum.requests.dto;

import lombok.Data;
import ru.practicum.util.enam.EventRequestStatus;

import java.util.Set;

@Data
public class EventRequestStatusUpdateRequest {

    private Set<Long> requestIds;

    private EventRequestStatus status;
}