package ru.practicum.main.category.service;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(Long catId);

    CategoryDto update(Long catId, NewCategoryDto newCategoryDto);

    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}