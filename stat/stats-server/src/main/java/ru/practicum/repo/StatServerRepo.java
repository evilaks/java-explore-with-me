package ru.practicum.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.StatisticsReportDto;
import ru.practicum.model.StatEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface StatServerRepo extends JpaRepository<StatEvent, Long> {

    @Query("SELECT new ru.practicum.dto.StatisticsReportDto(e.app, e.uri, COUNT(e)) " +
            "FROM StatEvent e WHERE e.timestamp BETWEEN :startDate AND :endDate " +
            "AND e.uri IN :uris GROUP BY e.app, e.uri ORDER BY COUNT(e) DESC")
    List<StatisticsReportDto> countEventsByDatesAndUris(@Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate,
                                                        @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.dto.StatisticsReportDto(e.app, e.uri, COUNT(e)) " +
            "FROM StatEvent e WHERE e.timestamp BETWEEN :startDate AND :endDate " +
            "GROUP BY e.app, e.uri ORDER BY COUNT(e) DESC")
    List<StatisticsReportDto> countEventsByDates(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new ru.practicum.dto.StatisticsReportDto(e.app, e.uri, COUNT(DISTINCT e.ip))" +
            "FROM StatEvent e WHERE e.timestamp BETWEEN :startDate AND :endDate " +
            "AND e.uri IN :uris GROUP BY e.app, e.uri ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<StatisticsReportDto> countEventsByDatesAndUrisWithUniqueIps(@Param("startDate") LocalDateTime startDate,
                                                                     @Param("endDate") LocalDateTime endDate,
                                                                     @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.dto.StatisticsReportDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM StatEvent e WHERE e.timestamp BETWEEN :startDate AND :endDate " +
            "GROUP BY e.app, e.uri ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<StatisticsReportDto> countEventsByDatesWithUniqueIps(@Param("startDate") LocalDateTime startDate,
                                                              @Param("endDate") LocalDateTime endDate);


}
