package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.model.tables.pojos.ProductVariant;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {
    ProductVariant toProductVariant(ProductRequest.VariantsRequest variantsRequest);

    List<ProductVariant> toProductVariant(List<ProductRequest.VariantsRequest> variantsRequests);

}
