package com.example.moduleapp.service;

import com.example.moduleapp.data.request.OrderRequest;
import com.example.moduleapp.data.request.OrderStatusRequest;
import io.reactivex.rxjava3.core.Single;

import java.util.Map;

public interface OrderService {
    Single<String> create(OrderRequest orderRequest);

    Single<String> updateStatus(OrderStatusRequest orderStatusRequest);

}
