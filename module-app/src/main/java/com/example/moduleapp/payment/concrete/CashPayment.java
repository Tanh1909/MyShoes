package com.example.moduleapp.payment.concrete;

import com.example.common.context.UserPrincipal;
import com.example.moduleapp.config.constant.OrderEnum;
import com.example.moduleapp.config.constant.PaymentMethodEnum;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.model.tables.pojos.Payment;
import com.example.moduleapp.payment.abstracts.PaymentAbstract;
import com.example.moduleapp.repository.impl.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CashPayment extends PaymentAbstract {
    private final OrderRepository orderRepository;

    @Override
    public PaymentMethodEnum getPaymentMethod() {
        return PaymentMethodEnum.CASH;
    }

    @Override
    public PaymentResponse handlePaymentResponse(Order order, UserPrincipal userPrincipal, Payment paymentResult, BigDecimal totalAmount) {
        order.setStatus(OrderEnum.PAYMENT_CONFIRMED.getValue());
        orderRepository.updateBlocking(order.getId(), order);
        return PaymentResponse.builder()
                .success(Boolean.TRUE)
                .build();
    }
}
