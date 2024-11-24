package com.example.moduleapp.repository.impl;

import com.example.moduleapp.config.constant.PaymentEnum;
import com.example.moduleapp.model.tables.pojos.Payment;
import com.example.moduleapp.repository.IPaymentRepository;
import com.example.moduleapp.repository.IRxPaymentRepository;
import com.example.repository.JooqRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import static com.example.moduleapp.model.Tables.PAYMENT;

@Repository
@RequiredArgsConstructor
public class PaymentRepository extends JooqRepository<Payment, Integer>
        implements IRxPaymentRepository, IPaymentRepository {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return PAYMENT;
    }

    @Override
    public Boolean findPaymentSuccessBlocking(Integer orderId) {
        return getDSLContext()
                .fetchExists(getTable(), PAYMENT.ORDER_ID.eq(orderId)
                        .and(PAYMENT.PAYMENT_STATUS.eq(PaymentEnum.SUCCESS.getValue())));
    }
}
