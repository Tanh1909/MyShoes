package com.example.moduleapp.controller;

import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.data.request.PaymentRequest;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ApiResponse<PaymentResponse> pay(@RequestBody PaymentRequest paymentRequest) {
        return ApiResponse.success(paymentService.pay(paymentRequest));
    }
}
