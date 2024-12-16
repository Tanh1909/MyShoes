package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.response.OrderPaymentResponse;
import com.example.moduleapp.data.response.OrderResponse;
import com.example.moduleapp.model.tables.pojos.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toOrderResponse(Order order);

    OrderPaymentResponse toOrderPaymentResponse(Order order);
}
