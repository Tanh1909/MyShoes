package com.example.moduleapp.controller;

import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.data.request.OrderRequest;
import com.example.moduleapp.data.request.OrderStatusRequest;
import com.example.moduleapp.service.OrderService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Single<ApiResponse<String>> create(@RequestBody OrderRequest orderRequest) {
        return orderService.create(orderRequest).map(ApiResponse::success);
    }

    @PatchMapping("/status")
    public Single<ApiResponse<String>> update(@RequestBody OrderStatusRequest orderStatusRequest) {
        return orderService.updateStatus(orderStatusRequest).map(ApiResponse::success);
    }
}
