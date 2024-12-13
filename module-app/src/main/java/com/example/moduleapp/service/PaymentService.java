package com.example.moduleapp.service;

import com.example.moduleapp.config.vnpay.VNPayReturnParams;
import com.example.moduleapp.data.request.PaymentRequest;
import com.example.moduleapp.data.response.PaymentResponse;
import io.reactivex.rxjava3.core.Single;

public interface PaymentService {
    Single<PaymentResponse> pay(PaymentRequest paymentRequest);

    Single<String> verify(VNPayReturnParams vnPayReturnParams);

}
