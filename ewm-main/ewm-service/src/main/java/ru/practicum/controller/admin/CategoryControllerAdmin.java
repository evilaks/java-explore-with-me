package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoryService;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryControllerAdmin {

    private final CategoryService categoryService;

    // POST /admin/categories
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Create category: {}", categoryDto);
        return new ResponseEntity<>(categoryService.create(categoryDto), HttpStatus.CREATED);
    }

    // DELETE /admin/categories/{catId}
    @DeleteMapping(path = "/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long catId) {
        log.info("Delete category with id: {}", catId);
        categoryService.delete(catId);
        return ResponseEntity.noContent().build();
    }

    // PATCH /admin/categories/{catId}
    @PatchMapping(path = "/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long catId, @RequestBody CategoryDto categoryDto) {
        log.info("Update category with id: {}", catId);
        categoryDto.setId(catId);
        return new ResponseEntity<>(categoryService.update(categoryDto), HttpStatus.OK);
    }
}
