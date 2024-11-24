package com.example.moduleapp.repository.impl;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.template.RxTemplate;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.repository.IRxOrderRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
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

    @Override
    public Single<PageResponse<Order>> findByUserIdAndStatus(Integer userId, String status, PageRequest pageRequest) {
        int totalRecords = getTotalRecords();
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int totalPage = (int) Math.ceil(totalRecords * 1f / size);
        int offset = page * size;
        return RxTemplate.rxSchedulerIo(() ->
                        getDSLContext().select()
                                .from(getTable())
                                .where(ORDER.USER_ID.eq(userId.longValue()).and(ORDER.STATUS.eq(status)))
                                .offset(offset)
                                .limit(size)
                                .fetchInto(pojoClass))
                .map(toPageResponse(page, size, totalPage));

    }
}
