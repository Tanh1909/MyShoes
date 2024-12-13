package com.example.moduleapp.repository.impl;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.model.tables.pojos.Cart;
import com.example.moduleapp.repository.IRxCartRepository;
import com.example.repository.JooqRepository;
import com.example.repository.utils.SQLQueryUtils;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.CART;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.when;

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

    @Override
    public Single<PageResponse<Cart>> findByUserId(Long userId, PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int offset = pageRequest.getOffset();
        Condition condition = CART.USER_ID.eq(userId);
        return Single.zip(
                getTotalRecords(condition),
                rxSchedulerIo(() -> getDSLContext()
                        .select()
                        .from(getTable())
                        .where(condition)
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

    @Override
    public Single<Integer> insertOrUpdate(Cart cart,Integer quantity, Integer stock) {
        return rxSchedulerIo(() -> getDSLContext()
                .insertInto(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), cart))
                .onDuplicateKeyUpdate()
                .set(
                        field("quantity"),
                        when(
                                field("quantity").add(quantity).le(stock),
                                field("quantity").add(quantity)
                        )
                                .otherwise(stock)
                )
                .execute()
        );
    }
}
