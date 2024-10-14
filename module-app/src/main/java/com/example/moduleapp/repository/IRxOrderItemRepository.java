package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.OrderItem;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface IRxOrderItemRepository extends IRxJooqRepository<OrderItem, Integer> {
    Single<List<OrderItem>> findByOrderId(Integer orderId);
}
