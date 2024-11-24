package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.OrderItem;
import com.example.repository.IBlockingRepository;

import java.util.List;

public interface IOrderItemRepository extends IBlockingRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderIdBlocking(Integer orderId);

}
