package com.example.moduleapp.service.impl;

import com.example.moduleapp.data.mapper.CategoryMapper;
import com.example.moduleapp.data.request.CategoryRequest;
import com.example.moduleapp.data.response.CategoryResponse;
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
}
