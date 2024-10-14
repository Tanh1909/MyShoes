package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.PaymentMethod;
import com.example.moduleapp.repository.IRxPaymentMethodRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.PAYMENT_METHOD;
import static java.util.Optional.ofNullable;

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

    @Override
    public Single<Optional<PaymentMethod>> findByName(String name) {
        return rxSchedulerIo(() -> ofNullable(getDSLContext()
                .select()
                .from(getTable())
                .where(PAYMENT_METHOD.NAME.eq(name))
                .fetchOneInto(pojoClass))
        );
    }
}
