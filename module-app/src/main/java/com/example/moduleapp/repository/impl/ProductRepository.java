package com.example.moduleapp.repository.impl;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.config.constant.OrderEnum;
import com.example.moduleapp.model.tables.pojos.Product;
import com.example.moduleapp.repository.IProductRepository;
import com.example.moduleapp.repository.IRxProductRepository;
import com.example.repository.JooqRepository;
import com.example.repository.utils.SQLQueryUtils;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
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
public class ProductRepository extends JooqRepository<Product, Integer>
        implements IRxProductRepository, IProductRepository {
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
    public Condition filterActive() {
        return super.filterActive().and(PRODUCT.DELETED_AT.isNull());
    }

    @Override
    public Single<Map<Integer, Integer>> getNumberOfPaid(Collection<Integer> productIds) {
        return rxSchedulerIo(() -> getDSLContext()
                .select(ORDER_ITEM.PRODUCT_ID, DSL.count(ORDER_ITEM.PRODUCT_ID).as("count"))
                .from(ORDER_ITEM)
                .join(ORDER).on(ORDER_ITEM.ORDER_ID.eq(ORDER.ID))
                .where(ORDER.STATUS.eq(OrderEnum.SUCCESS.getValue()))
                .groupBy(ORDER_ITEM.PRODUCT_ID)
                .fetchMap(ORDER_ITEM.PRODUCT_ID, DSL.field("count", Integer.class))
        );
    }

    @Override
    public Single<Integer> getNumberOfPaid(Integer productId) {
        return rxSchedulerIo(() -> getDSLContext()
                .select(DSL.count(ORDER_ITEM.PRODUCT_ID).as("count"))
                .from(ORDER_ITEM)
                .join(ORDER).on(ORDER_ITEM.ORDER_ID.eq(ORDER.ID))
                .where(ORDER.STATUS.eq(OrderEnum.SUCCESS.getValue()).and(ORDER_ITEM.PRODUCT_ID.eq(productId)))
                .fetchOneInto(Integer.class)
        );
    }

    @Override
    public Single<PageResponse<Product>> findByCategoryId(Integer categoryId, PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int offset = pageRequest.getOffset();
        Condition condition = PRODUCT.CATEGORY_ID.eq(categoryId);
        return Single.zip(
                getTotalRecords(condition),
                rxSchedulerIo(() -> getDSLContext()
                        .select()
                        .from(getTable())
                        .where(condition.and(filterActive()))
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
}
