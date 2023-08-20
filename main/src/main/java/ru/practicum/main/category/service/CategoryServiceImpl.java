package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exceptions.DataConflictException;
import ru.practicum.main.exceptions.NotFoundException;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.main.category.mapper.CategoryMapper.toCategory;
import static ru.practicum.main.category.mapper.CategoryMapper.toCategoryDto;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        checkName(newCategoryDto.getName());
        Category category = toCategory(newCategoryDto);
        Category newCategory = categoryRepository.save(category);
        return toCategoryDto(newCategory);
    }

    @Override
    public void delete(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория с id" + catId + "не найдена в базе данных"));
        if (eventRepository.existsByCategory(category)) {
            throw new DataConflictException("Нельзя удалить категорию: существуют события, связанные с категорией");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto update(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория с id" + catId + "не найдена в базе данных"));
        if (categoryDto != null && !categoryDto.getName().equals(category.getName())) {
            checkName(categoryDto.getName());
            category.setName(categoryDto.getName());
        }
        Category updCategory = categoryRepository.save(category);
        return toCategoryDto(updCategory);
    }

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest).stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория с id" + catId + "не найден в базе данных"));
        return toCategoryDto(category);
    }

    private void checkName(String name) {
        Category category = categoryRepository.findByName(name);
        if (category != null) {
            throw new DataConflictException("Название категории уже используется");
        }
    }
}