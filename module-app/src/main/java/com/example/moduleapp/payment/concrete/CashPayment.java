package com.example.moduleapp.payment.concrete;

import com.example.moduleapp.config.constant.PaymentMethodEnum;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.payment.abstracts.PaymentAbstract;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashPayment extends PaymentAbstract {
    @Override
    public PaymentMethodEnum getPaymentMethod() {
        return PaymentMethodEnum.CASH;
    }

    @Override
    public PaymentResponse pay(String orderId, double amount, UserDetails userDetails) {
        return null;
    }
}
