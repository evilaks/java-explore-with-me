package ru.practicum.dto.compilation;

import lombok.Data;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Data
public class CompilationDtoToShow {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}
