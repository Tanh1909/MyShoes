package com.example.moduleapp.service;

import com.example.moduleapp.config.vnpay.VNPayReturnParams;
import com.example.moduleapp.data.request.PaymentRequest;
import com.example.moduleapp.data.response.PaymentResponse;
import io.reactivex.rxjava3.core.Single;

import java.util.Map;

public interface PaymentService {
    Single<PaymentResponse> pay(PaymentRequest paymentRequest);

    Single<String> verify(Map<String,String> vnPayReturnParams);

}
