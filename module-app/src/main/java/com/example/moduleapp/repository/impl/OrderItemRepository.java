package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.OrderItem;
import com.example.moduleapp.repository.IRxOrderItemRepository;
import com.example.repository.JooqRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import static com.example.moduleapp.model.Tables.ORDER_ITEM;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository extends JooqRepository<OrderItem, Integer> implements IRxOrderItemRepository {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return ORDER_ITEM;
    }
}
