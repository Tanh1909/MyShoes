package com.example.moduleapp.repository;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.config.constant.OrderEnum;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

public interface IRxOrderRepository extends IRxJooqRepository<Order, Integer> {
    Single<PageResponse<Order>> findByUserIdAndStatus(Integer userId, String status, PageRequest pageRequest);

    Single<PageResponse<Order>> findOrderPaymentResponses(OrderEnum orderEnum, PageRequest pageRequest);

    Single<PageResponse<Order>> findOrderPaymentSuccess(PageRequest pageRequest);
}
