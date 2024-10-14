package com.example.moduleapp.service;

import com.example.moduleapp.data.request.OrderRequest;
import io.reactivex.rxjava3.core.Single;

public interface OrderService {
    Single<String> create(OrderRequest orderRequest);
}
