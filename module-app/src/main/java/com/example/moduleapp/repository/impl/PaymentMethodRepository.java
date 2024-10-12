package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.PaymentMethod;
import com.example.moduleapp.repository.IRxPaymentMethodRepository;
import com.example.repository.JooqRepository;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import static com.example.moduleapp.model.Tables.PAYMENT_METHOD;

@Repository
@AllArgsConstructor
public class PaymentMethodRepository extends JooqRepository<PaymentMethod, Integer> implements IRxPaymentMethodRepository {
    private final DSLContext dslContext;

    @Override
    protected DSLContext getDSLContext() {
        return dslContext;
    }

    @Override
    protected Table getTable() {
        return PAYMENT_METHOD;
    }
}
