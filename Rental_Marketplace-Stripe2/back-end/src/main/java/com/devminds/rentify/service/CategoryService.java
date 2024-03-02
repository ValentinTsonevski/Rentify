package com.devminds.rentify.service;

import com.devminds.rentify.dto.CategoryDto;

import com.devminds.rentify.entity.Category;
import com.devminds.rentify.exception.CategoryNotFoundException;
import com.devminds.rentify.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category with %d id not found.";
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }


    public List<CategoryDto> getAllCategories() {
        return categoryRepository
                .findAll()
                .stream()
                .map(this::mapCategoryToCategoryDto)
                .toList();

    }

    public CategoryDto getCategoryById(long id) {
        return categoryRepository.findById(id)
                .map(this::mapCategoryToCategoryDto)
                .orElseThrow(() -> new CategoryNotFoundException(String.format(CATEGORY_NOT_FOUND_MESSAGE, id)));
    }

    private CategoryDto mapCategoryToCategoryDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

}
