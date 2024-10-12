package com.example.moduleapp.payment.abstracts;

import com.example.moduleapp.config.constant.PaymentMethodEnum;
import com.example.moduleapp.data.response.PaymentResponse;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class PaymentAbstract {
    public abstract PaymentMethodEnum getPaymentMethod();
    public abstract PaymentResponse pay(String orderId, double amount, UserDetails userDetails);
}
