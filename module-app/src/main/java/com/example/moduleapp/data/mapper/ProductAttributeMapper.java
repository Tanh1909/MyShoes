package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.model.tables.pojos.ProductAttribute;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {
    ProductAttribute toProductAttribute(ProductRequest.AttributeRequest attributeRequest);

    List<ProductAttribute> toProductAttribute(Collection<ProductRequest.AttributeRequest> attributeRequests);
}
