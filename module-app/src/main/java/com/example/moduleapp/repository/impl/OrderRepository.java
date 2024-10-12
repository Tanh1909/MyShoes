package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.repository.IRxOrderRepository;
import com.example.repository.JooqRepository;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import static com.example.moduleapp.model.Tables.ORDER;

@Repository
@AllArgsConstructor
public class OrderRepository extends JooqRepository<Order, Integer> implements IRxOrderRepository {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return ORDER;
    }
}
