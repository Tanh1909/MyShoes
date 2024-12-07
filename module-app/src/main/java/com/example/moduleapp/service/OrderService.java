package com.example.moduleapp.service;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.data.request.OrderRequest;
import com.example.moduleapp.data.request.OrderStatusRequest;
import com.example.moduleapp.data.response.OrderResponse;
import io.reactivex.rxjava3.core.Single;

public interface OrderService {
    Single<String> create(OrderRequest orderRequest);

    Single<String> updateStatus(OrderStatusRequest orderStatusRequest);

    Single<PageResponse<OrderResponse>> getOrderResponseByStatus(String status, PageRequest pageRequest);

}
