package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.data.response.ProductDetailResponse;
import com.example.moduleapp.data.response.ProductResponse;
import com.example.moduleapp.model.tables.pojos.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductRequest productRequest);

    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponse(List<Product> productList);

    ProductDetailResponse toProductDetailResponse(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toProduct(@MappingTarget Product product, ProductRequest productRequest);
}
