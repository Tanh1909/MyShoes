package com.example.moduleapp.repository.impl;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.template.RxTemplate;
import com.example.moduleapp.model.tables.pojos.Cart;
import com.example.moduleapp.repository.IRxCartRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
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

    @Override
    public Single<PageResponse<Cart>> findByUserId(Long userId, PageRequest pageRequest) {
        int totalRecords = getTotalRecords();
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int totalPage = (int) Math.ceil(totalRecords * 1f / size);
        int offset = page * size;
        return RxTemplate.rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where()
                .offset(offset)
                .limit(size)
                .fetchInto(pojoClass)
        ).map(toPageResponse(page, size, totalPage));
    }
}
