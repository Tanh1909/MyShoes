package com.example.moduleapp.service.impl;

import com.example.common.utils.ValidateUtils;
import com.example.moduleapp.data.mapper.CategoryMapper;
import com.example.moduleapp.data.request.CategoryRequest;
import com.example.moduleapp.data.request.CategoryUpdateRequest;
import com.example.moduleapp.data.response.CategoryResponse;
import com.example.moduleapp.model.tables.pojos.Category;
import com.example.moduleapp.repository.impl.CategoryRepository;
import com.example.moduleapp.service.CategoryService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Single<String> createCategory(CategoryRequest categoryRequest) {
        categoryRepository.insertBlocking(categoryMapper.toCategory(categoryRequest));
        return Single.just("SUCCESS");
    }

    @Override
    public Single<List<CategoryResponse>> getAllCategories() {
        return categoryRepository.findAll()
                .map(categoryMapper::toCategoryResponses);
    }

    @Override
    public Single<String> deleteCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .flatMap(categoryOptional -> {
                    ValidateUtils.getOptionalValue(categoryOptional, Category.class);
                    return categoryRepository.deleteById(categoryId);
                })
                .map(integer -> "SUCCESS");
    }

    @Override
    public Single<String> updateCategory(Integer categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        return categoryRepository.findById(categoryId)
                .flatMap(categoryOptional -> {
                    Category category = ValidateUtils.getOptionalValue(categoryOptional, Category.class);
                    categoryMapper.toCategory(category, categoryUpdateRequest);
                    return categoryRepository.update(categoryId, category);
                })
                .map(integer -> "SUCCESS");
    }
}
