package ru.practicum.dto.category;

import org.mapstruct.Mapper;
import ru.practicum.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryDtoMapper {

        CategoryDto toDto(Category category);

        Category toEntity(CategoryDto categoryDto);
}
