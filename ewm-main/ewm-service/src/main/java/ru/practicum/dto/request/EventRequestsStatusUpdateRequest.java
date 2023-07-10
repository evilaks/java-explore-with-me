package ru.practicum.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestsStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
