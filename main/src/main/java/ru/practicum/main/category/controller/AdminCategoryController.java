package ru.practicum.main.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@Validated
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Категория добавлена: {}", newCategoryDto.getName());
        return categoryService.create(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        log.debug("Удаление категории");
        categoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId, @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Категория изменена: {}", newCategoryDto.getName());
        return categoryService.update(catId, newCategoryDto);
    }
}