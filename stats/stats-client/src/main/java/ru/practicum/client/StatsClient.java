package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import ru.practicum.dto.StatisticsEventDto;
import ru.practicum.dto.StatisticsReportDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StatsClient {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    protected final RestTemplate rest;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

    }

    public List<StatisticsReportDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        String urisString = String.join(",", uris);

        Map<String, Object> parameters = Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "uris", urisString,
                "unique", unique
        );

        try {
            String path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
            return getArray(path, parameters);

        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Error getting statistics", e);
        }

    }

    public ResponseEntity<StatisticsEventDto> saveEvent(StatisticsEventDto body) {
        log.info("Saving statistics event: {}", body);
        return post("/hit", body);
    }

    private List<StatisticsReportDto> getArray(String path, Map<String, Object> parameters) {
        ResponseEntity<List<StatisticsReportDto>> responseEntity =
                rest.exchange(
                        path,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        },
                        parameters
                );

        return responseEntity.getBody();
    }

    private ResponseEntity<StatisticsEventDto> post(String path, StatisticsEventDto body) {
        return makeAndSendRequest(HttpMethod.POST, path, body);
    }

    private ResponseEntity<StatisticsEventDto> makeAndSendRequest(HttpMethod method, String path, StatisticsEventDto body) {
        HttpEntity<StatisticsEventDto> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<StatisticsEventDto> statServerResponse;
        try {
            statServerResponse = rest.exchange(path, method, requestEntity, StatisticsEventDto.class);
        } catch (HttpStatusCodeException e) {
           throw new RuntimeException("Error sending request to statistics server", e);
        }
        return prepareGatewayResponse(statServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<StatisticsEventDto> prepareGatewayResponse(ResponseEntity<StatisticsEventDto> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
