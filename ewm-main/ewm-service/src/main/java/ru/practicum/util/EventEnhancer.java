package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatisticsReportDto;
import ru.practicum.dto.event.EventDtoMapper;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.request.RequestStatus;
import ru.practicum.repo.ParticipationRequestRepo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventEnhancer {

    private final StatsClient statsClient;
    private final EventDtoMapper eventDtoMapper;
    private final ParticipationRequestRepo partRequestRepo;

    public List<EventFullDto> addViewsAndConfirmedRequests(List<EventFullDto> events) {
        List<String> eventUris = events.stream()
                .map(eventFullDto -> ("/events/" + eventFullDto.getId()))
                .collect(Collectors.toList());

        List<StatisticsReportDto> stats = statsClient.getStats(LocalDateTime.now().minusYears(100),
                LocalDateTime.now(), eventUris, true);

        Map<String, Long> uriToHitsMap = stats.stream()
                .collect(Collectors.toMap(StatisticsReportDto::getUri, StatisticsReportDto::getHits));

        for (EventFullDto event : events) {
            String uri = "/events/" + event.getId();
            event.setViews(uriToHitsMap.getOrDefault(uri, 0L));
            Long confirmedRequests = partRequestRepo.countParticipationRequestsByEventAndStatus(
                    eventDtoMapper.toEntity(event), RequestStatus.CONFIRMED);
            event.setConfirmedRequests(confirmedRequests);
        }

        return events;
    }

    public EventFullDto addViewsAndConfirmedRequests(EventFullDto event) {
        String uri = "/events/" + event.getId();

        List<StatisticsReportDto> statReport = statsClient.getStats(LocalDateTime.now().minusYears(100),
                LocalDateTime.now(), Collections.singletonList(uri), true);
        if (statReport.isEmpty()) {
            event.setViews(0L);
        } else {
            event.setViews(statReport.get(0).getHits());
        }

        Long confirmedRequests = partRequestRepo.countParticipationRequestsByEventAndStatus(
                eventDtoMapper.toEntity(event), RequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedRequests);

        return event;
    }
}
