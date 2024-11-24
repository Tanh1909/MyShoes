package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.response.OrderItemResponse;
import com.example.moduleapp.model.tables.pojos.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
