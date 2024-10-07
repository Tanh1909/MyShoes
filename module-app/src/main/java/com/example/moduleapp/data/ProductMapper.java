package com.example.moduleapp.data;

import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.model.tables.pojos.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductRequest productRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toProduct(@MappingTarget Product product, ProductRequest productRequest);
}
