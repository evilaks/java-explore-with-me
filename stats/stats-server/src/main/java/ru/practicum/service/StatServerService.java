package ru.practicum.service;

import ru.practicum.dto.StatisticsEventDto;
import ru.practicum.dto.StatisticsReportDto;

import java.util.List;

public interface StatServerService {

    StatisticsEventDto save(StatisticsEventDto statisticsEventDto);

    List<StatisticsReportDto> getStatistics(String start, String end, List<String> uris, Boolean unique);
}
