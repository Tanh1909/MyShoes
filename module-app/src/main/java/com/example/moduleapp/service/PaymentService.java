package com.example.moduleapp.service;

import com.example.moduleapp.data.request.PaymentRequest;
import com.example.moduleapp.data.response.PaymentResponse;
import io.reactivex.rxjava3.core.Single;

import java.time.LocalDateTime;

public interface PaymentService {
    Single<PaymentResponse> pay(PaymentRequest paymentRequest);

    Single<String> handleVNPayCallback(Integer paymentId, LocalDateTime paidAt, String responseCode);
}
