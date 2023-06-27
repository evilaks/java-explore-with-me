package ru.practicum.dto;

import lombok.Data;

@Data
public class StatisticsReportDto {
    private String app;
    private String uri;
    private Long hits;

    public StatisticsReportDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
