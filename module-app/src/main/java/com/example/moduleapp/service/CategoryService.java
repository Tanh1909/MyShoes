package com.example.moduleapp.service;

import com.example.moduleapp.data.request.CategoryRequest;
import com.example.moduleapp.data.response.CategoryResponse;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface CategoryService {
    Single<String> createCategory(CategoryRequest categoryRequest);

    Single<List<CategoryResponse>> getAllCategories();
}
