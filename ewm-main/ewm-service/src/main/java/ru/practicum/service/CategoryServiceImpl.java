package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryDtoMapper;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.repo.CategoryRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDtoMapper categoryDtoMapper;
    private final CategoryRepo categoryRepo;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {

        validateCategory(categoryDto);

        return categoryDtoMapper.toDto(categoryRepo.save(categoryDtoMapper.toEntity(categoryDto)));
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {

        validateCategory(categoryDto);

        if (categoryRepo.existsById(categoryDto.getId())) {
            return categoryDtoMapper.toDto(categoryRepo.save(categoryDtoMapper.toEntity(categoryDto)));
        } else {
            throw new NotFoundException("Category not found", "Category with id " + categoryDto.getId() + " not found");
        }

    }

    @Override
    public void delete(Long id) {

        if (!categoryRepo.existsById(id)) {
            throw new NotFoundException("Category not found", "Category with id " + id + " not found");
        }

        categoryRepo.deleteById(id);
    }

    @Override
    public CategoryDto findById(Long id) {
        return null;
    }

    @Override
    public List<CategoryDto> findAll() {
        return null;
    }

    private void validateCategory(CategoryDto categoryDto) {
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            throw new BadRequestException("Category name is empty", "Category name is empty");
        }
    }
}
