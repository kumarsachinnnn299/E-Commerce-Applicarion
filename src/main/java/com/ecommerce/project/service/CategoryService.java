package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.payload.CategoryDTO;
import com.ecommerce.project.model.payload.CategoryResponse;

import java.util.List;
public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize);
    CategoryDTO createCategory (CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
