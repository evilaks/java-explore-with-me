package ru.practicum.controller.unauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class CategoryControllerUnauth {

    private final CategoryService categoryService;

    // GET /categories
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                                           @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get categories with params from={} and size={}", from, size);
        return ResponseEntity.ok().body(categoryService.findAll(from, size));
    }

    // GET /categories/{catId}
    @GetMapping(path = "/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long catId) {
        log.info("Get category with id: {}", catId);
        return ResponseEntity.ok().body(categoryService.findById(catId));
    }
}
