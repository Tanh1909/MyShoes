package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.response.PaymentOrderResponse;
import com.example.moduleapp.model.tables.pojos.Payment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toPayment(@MappingTarget Payment target, Payment source);

    PaymentOrderResponse toPaymentOrderResponse(Payment payment);
}
