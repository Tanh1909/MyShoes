package com.example.moduleapp.controller;

import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.config.vnpay.VNPayReturnParams;
import com.example.moduleapp.data.request.PaymentRequest;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.service.PaymentService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public Single<ApiResponse<PaymentResponse>> pay(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.pay(paymentRequest).map(ApiResponse::success);
    }

    @GetMapping("/vnpay/verify")
    public Single<String> handleVerifyVNPay(VNPayReturnParams vnPayReturnParams) {
        return paymentService.verify(vnPayReturnParams);
    }
}
