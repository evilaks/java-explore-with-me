package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatEventDtoMapper;
import ru.practicum.dto.StatisticsEventDto;
import ru.practicum.dto.StatisticsReportDto;
import ru.practicum.model.StatEvent;
import ru.practicum.repo.StatServerRepo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServerService {

    private final StatServerRepo repository;
    private final StatEventDtoMapper statEventDtoMapper;

    @Transactional
    public StatisticsEventDto save(StatisticsEventDto eventDto) {
        StatEvent statEvent = statEventDtoMapper.toEntity(eventDto);
        return statEventDtoMapper.toDto(repository.save(statEvent));
    }

    public List<StatisticsReportDto> getStatistics(String start, String end, List<String> uris, Boolean unique) {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(start, formatter);
            LocalDateTime endDate = LocalDateTime.parse(end, formatter);

            if (!this.isValidDates(startDate, endDate)) {
                throw new IllegalArgumentException("End date must be after start date");
            }

            if (unique && uris.contains("ALL")) {
                return repository.countEventsByDatesWithUniqueIps(startDate, endDate);
            } else if (unique && !uris.contains("ALL")) {
                return repository.countEventsByDatesAndUrisWithUniqueIps(startDate, endDate, uris);
            } else if (!unique && uris.contains("ALL")) {
                return repository.countEventsByDates(startDate, endDate);
            } else {
                return repository.countEventsByDatesAndUris(startDate, endDate, uris);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format");
        }
    }

    private boolean isValidDates(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.isBefore(endDate);
    }


}
