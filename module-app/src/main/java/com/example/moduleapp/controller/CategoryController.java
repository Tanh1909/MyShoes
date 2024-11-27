package com.example.moduleapp.controller;

import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.data.request.CategoryRequest;
import com.example.moduleapp.data.request.CategoryUpdateRequest;
import com.example.moduleapp.data.response.CategoryResponse;
import com.example.moduleapp.service.CategoryService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Single<ApiResponse<String>> create(@RequestBody CategoryRequest categoryRequest) {
        return categoryService.createCategory(categoryRequest).map(ApiResponse::success);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse<List<CategoryResponse>>> findAll() {
        return categoryService.getAllCategories().map(ApiResponse::success);
    }

    @DeleteMapping("/{id}")
    public Single<ApiResponse<String>> deleteById(@PathVariable Integer id) {
        return categoryService.deleteCategory(id).map(ApiResponse::success);
    }

    @PatchMapping("/{id}")
    public Single<ApiResponse<String>> updateById(@PathVariable Integer id, @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        return categoryService.updateCategory(id, categoryUpdateRequest).map(ApiResponse::success);
    }


}
