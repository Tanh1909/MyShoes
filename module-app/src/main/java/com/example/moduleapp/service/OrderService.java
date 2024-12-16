package com.example.moduleapp.service;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.data.request.OrderRequest;
import com.example.moduleapp.data.request.OrderStatusRequest;
import com.example.moduleapp.data.response.OrderCreateResponse;
import com.example.moduleapp.data.response.OrderPaymentResponse;
import com.example.moduleapp.data.response.OrderResponse;
import io.reactivex.rxjava3.core.Single;

public interface OrderService {
    Single<OrderCreateResponse> create(OrderRequest orderRequest);

    Single<String> updateStatus(OrderStatusRequest orderStatusRequest);

    Single<PageResponse<OrderResponse>> getOrderResponseByUserAndStatus(String status, PageRequest pageRequest);

    Single<PageResponse<OrderPaymentResponse>> getOrderPaymentResponse(String status, PageRequest pageRequest);

    Single<PageResponse<OrderPaymentResponse>> getOrderPaymentSuccess(PageRequest pageRequest);

    Single<OrderPaymentResponse> getById(Integer id);
}
