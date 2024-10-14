package com.example.moduleapp.controller;

import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.config.vnpay.VNPayReturnParams;
import com.example.moduleapp.data.request.PaymentRequest;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.service.PaymentService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public Single<ApiResponse<PaymentResponse>> pay(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.pay(paymentRequest).map(ApiResponse::success);
    }

    @GetMapping("/vnpay-callback")
    public Single<String> handleVnPayCallback(VNPayReturnParams vnPayReturnParams) {
        Integer paymentId = Integer.valueOf(vnPayReturnParams.getVnp_TxnRef());
        LocalDateTime paidAt = LocalDateTime.now();
        String responseCode = vnPayReturnParams.getVnp_ResponseCode();
        return paymentService.handleVNPayCallback(paymentId, paidAt, responseCode);
    }
}
