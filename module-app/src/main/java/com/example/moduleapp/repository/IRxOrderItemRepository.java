package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.OrderItem;
import com.example.repository.IRxJooqRepository;

public interface IRxOrderItemRepository extends IRxJooqRepository<OrderItem, Integer> {
}
