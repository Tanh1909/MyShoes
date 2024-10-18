package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.Product;
import com.example.moduleapp.repository.IRxProductRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.*;

@Repository
@RequiredArgsConstructor
public class ProductRepository extends JooqRepository<Product, Long> implements IRxProductRepository {
    private final DSLContext dslContext;

    @Override
    protected DSLContext getDSLContext() {
        return dslContext;
    }

    @Override
    protected Table getTable() {
        return PRODUCT;
    }

    @Override
    public Single<Map<Integer, Integer>> getNumberOfPaid(Collection<Integer> productIds) {
        return rxSchedulerIo(() -> getDSLContext()
                .select(ORDER_ITEM.PRODUCT_ID, DSL.count(ORDER_ITEM.PRODUCT_ID).as("count"))
                .from(ORDER_ITEM)
                .join(ORDER).on(ORDER_ITEM.ORDER_ID.eq(ORDER.ID))
                .groupBy(ORDER_ITEM.PRODUCT_ID)
                .fetchMap(ORDER_ITEM.PRODUCT_ID, DSL.field("count", Integer.class))
        );
    }
}
