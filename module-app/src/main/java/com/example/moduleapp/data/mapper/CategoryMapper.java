package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.CategoryRequest;
import com.example.moduleapp.data.request.CategoryUpdateRequest;
import com.example.moduleapp.data.response.CategoryResponse;
import com.example.moduleapp.model.tables.pojos.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest categoryRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toCategory(@MappingTarget Category category, CategoryUpdateRequest categoryUpdateRequest);

    CategoryResponse toCategoryResponse(Category category);

    List<CategoryResponse> toCategoryResponses(List<Category> categories);
}
