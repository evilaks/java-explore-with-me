package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryDtoMapper;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.repo.CategoryRepo;

import java.util.List;
import java.util.stream.Collectors;

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
        return categoryRepo.findById(id)
                .map(categoryDtoMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Category not found", "Category with id " + id + " not found"));
    }

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {

        int page = from > 0 ? from / size : 0;

        return categoryRepo.findAll(PageRequest.of(page, size)).stream()
                .map(categoryDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateCategory(CategoryDto categoryDto) {
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            throw new BadRequestException("Category name is empty", "Category name is empty");
        }

        if (categoryDto.getName().length() > 50) {
            throw new BadRequestException("Category name is too long", "Category name is too long");
        }
    }
}
