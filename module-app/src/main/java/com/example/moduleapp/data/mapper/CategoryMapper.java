package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.CategoryRequest;
import com.example.moduleapp.data.response.CategoryResponse;
import com.example.moduleapp.model.tables.pojos.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest categoryRequest);

    CategoryResponse toCategoryResponse(Category category);

    List<CategoryResponse> toCategoryResponses(List<Category> categories);
}
