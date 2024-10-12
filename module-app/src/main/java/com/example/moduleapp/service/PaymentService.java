package com.example.moduleapp.service;

import com.example.moduleapp.data.request.PaymentRequest;
import com.example.moduleapp.data.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse pay(PaymentRequest paymentRequest);
}
