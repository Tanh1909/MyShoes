package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.Tables;
import com.example.moduleapp.model.tables.pojos.Cart;
import com.example.moduleapp.repository.IRxCartRepository;
import com.example.repository.JooqRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import static com.example.moduleapp.model.Tables.CART;

@Repository
@RequiredArgsConstructor
public class CartRepository extends JooqRepository<Cart, Long> implements IRxCartRepository {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return CART;
    }

}
