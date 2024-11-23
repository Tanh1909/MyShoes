package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.CartRequest;
import com.example.moduleapp.model.tables.pojos.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart toCart(CartRequest cartRequest);
}
