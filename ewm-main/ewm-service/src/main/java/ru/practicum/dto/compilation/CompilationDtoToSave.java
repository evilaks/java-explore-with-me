package ru.practicum.dto.compilation;

import lombok.Data;

import java.util.List;

@Data
public class CompilationDtoToSave {
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
