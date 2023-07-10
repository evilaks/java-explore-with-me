package ru.practicum.service;

import ru.practicum.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(Long id);

    CategoryDto findById(Long id);

    List<CategoryDto> findAll(Integer from, Integer size);
}
