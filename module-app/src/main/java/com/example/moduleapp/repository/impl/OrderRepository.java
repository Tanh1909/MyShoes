package com.example.moduleapp.repository.impl;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.config.constant.OrderEnum;
import com.example.moduleapp.config.constant.PaymentEnum;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.repository.IRxOrderRepository;
import com.example.repository.JooqRepository;
import com.example.repository.utils.SQLQueryUtils;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.ORDER;
import static com.example.moduleapp.model.Tables.PAYMENT;

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

    @Override
    public Single<PageResponse<Order>> findByUserIdAndStatus(Integer userId, String status, PageRequest pageRequest) {
        Condition condition = ORDER.USER_ID.eq(userId.longValue()).and(ORDER.STATUS.eq(status));
        return findAllByCondition(pageRequest, condition);
    }

    @Override
    public Single<PageResponse<Order>> findOrderPaymentResponses(OrderEnum orderEnum, PageRequest pageRequest) {
        Condition condition = orderEnum == null ? DSL.trueCondition() : ORDER.STATUS.eq(orderEnum.getValue());
        return findAllByCondition(pageRequest, condition);
    }

    @Override
    public Single<PageResponse<Order>> findOrderPaymentSuccess(PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int offset = pageRequest.getOffset();
        Condition condition = ORDER.STATUS.ne(OrderEnum.SUCCESS.getValue()).and(PAYMENT.STATUS.eq(PaymentEnum.SUCCESS.getValue()));
        return Single.zip(
                getTotalRecordOfPaymentSuccess(condition),
                rxSchedulerIo(() -> getDSLContext()
                        .select(getTable().fields())
                        .from(getTable())
                        .join(PAYMENT).on(PAYMENT.ORDER_ID.eq(ORDER.ID))
                        .where(filterActive().and(condition))
                        .orderBy(SQLQueryUtils.getSortFields(pageRequest.getOrders(), getTable()))
                        .offset(offset)
                        .limit(size)
                        .fetchInto(pojoClass)
                ),
                (totalRecords, results) -> {
                    int totalPage = (int) Math.ceil(totalRecords * 1f / size);
                    return PageResponse.toPageResponse(results, page, size, totalPage, totalRecords);
                }
        );
    }

    private Single<Integer> getTotalRecordOfPaymentSuccess(Condition condition) {
        return rxSchedulerIo(() -> getDSLContext().selectCount()
                .from(getTable())
                .join(PAYMENT).on(PAYMENT.ORDER_ID.eq(ORDER.ID))
                .where(condition.and(filterActive()))
                .fetchOne(0, int.class)
        );
    }
}
