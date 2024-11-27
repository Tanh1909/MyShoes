package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.dto.ProductVariantDetail;
import com.example.moduleapp.data.request.CartUpdateRequest;
import com.example.moduleapp.data.response.CartResponse;
import com.example.moduleapp.model.tables.pojos.Cart;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toCart(@MappingTarget Cart cart, CartUpdateRequest cartUpdateRequest);

    CartResponse toCartResponse(Cart cart);

    CartResponse.ProductVariant toCartProductVariant(ProductVariantDetail productVariantDetail);

}
