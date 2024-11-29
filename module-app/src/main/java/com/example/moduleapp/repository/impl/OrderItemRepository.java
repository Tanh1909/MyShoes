package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.OrderItem;
import com.example.moduleapp.repository.IOrderItemRepository;
import com.example.moduleapp.repository.IRxOrderItemRepository;
import com.example.repository.JooqRepository;
import com.example.repository.utils.SQLQueryUtils;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.ORDER_ITEM;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository extends JooqRepository<OrderItem, Integer>
        implements IRxOrderItemRepository, IOrderItemRepository {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return ORDER_ITEM;
    }

    @Override
    public Single<List<OrderItem>> findByOrderId(Integer orderId) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(ORDER_ITEM.ORDER_ID.eq(orderId))
                .fetchInto(pojoClass)
        );
    }

    @Override
    public List<OrderItem> findByOrderIdBlocking(Integer orderId) {
        return getDSLContext()
                .select()
                .from(getTable())
                .where(ORDER_ITEM.ORDER_ID.eq(orderId))
                .fetchInto(pojoClass);
    }

    @Override
    public Single<List<OrderItem>> findByOrderIdIn(Collection<Integer> orderIds) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(ORDER_ITEM.ORDER_ID.in(orderIds))
                .fetchInto(pojoClass)
        );
    }

    @Override
    public Integer updateByCodeBlocking(String code, OrderItem orderItem) {
        return getDSLContext().update(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), orderItem))
                .where(ORDER_ITEM.CODE.eq(code))
                .execute();
    }
}
