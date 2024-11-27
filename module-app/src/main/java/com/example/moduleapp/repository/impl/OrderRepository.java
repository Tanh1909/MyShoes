package com.example.moduleapp.repository.impl;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.template.RxTemplate;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.repository.IRxOrderRepository;
import com.example.repository.JooqRepository;
import com.example.repository.utils.SQLQueryUtils;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
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

    @Override
    public Single<PageResponse<Order>> findByUserIdAndStatus(Integer userId, String status, PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int offset = pageRequest.getOffset();
        Condition condition = ORDER.USER_ID.eq(userId.longValue()).and(ORDER.STATUS.eq(status));
        return Single.zip(
                getTotalRecords(condition),
                RxTemplate.rxSchedulerIo(() ->
                        getDSLContext().select()
                                .from(getTable())
                                .where(condition)
                                .orderBy(SQLQueryUtils.getSortFields(pageRequest.getOrders(), getTable()))
                                .offset(offset)
                                .limit(size)
                                .fetchInto(pojoClass)),
                (totalRecords, results) -> {
                    int totalPage = (int) Math.ceil(totalRecords * 1f / size);
                    return PageResponse.toPageResponse(results, page, size, totalPage, totalRecords);
                }

        );
    }
}
