package com.example.repository;

import io.reactivex.rxjava3.core.Single;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.repository.utils.SQLQueryUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static com.example.common.template.RxTemplate.rxSchedulerIo;

@Log4j2
public abstract class JooqRepository<P, ID> implements
        IRxJooqRepository<P, ID>, IBlockingRepository<P, ID> {
    protected Class<P> pojoClass;
    protected Field<ID> idField;

    protected abstract DSLContext getDSLContext();

    protected abstract Table getTable();

    @PostConstruct
    public void init() {
        log.info("init class {}", this.getClass().getSimpleName());
        this.pojoClass = ((Class<P>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
        this.idField = (TableField) Arrays.stream(getTable().fields())
                .filter(field -> field.getName().equalsIgnoreCase("id"))
                .findFirst()
                .orElse(null);

    }

    @Override
    public Single<Integer> insert(P entity) {
        return rxSchedulerIo(() -> getDSLContext().insertInto(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), entity))
                .execute()
        );
    }

    @Override
    public Single<P> insertReturn(P entity) {
        return rxSchedulerIo(() -> getDSLContext().insertInto(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), entity))
                .returning()
                .fetchOne()
                .map(record -> record.into(pojoClass))
        );
    }

    @Override
    public Single<Integer> update(ID id, P entity) {
        return rxSchedulerIo(() -> getDSLContext().update(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), entity))
                .where(idField.eq(id))
                .execute()
        );
    }

    @Override
    public Single<P> updateReturn(ID id, P entity) {
        return rxSchedulerIo(() -> getDSLContext().update(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), entity))
                .where(idField.eq(id))
                .returning()
                .fetchOne()
                .map(record -> record.into(pojoClass))
        );
    }

    @Override
    public Single<Integer> deleteById(ID id) {
        return rxSchedulerIo(() -> getDSLContext().delete(getTable())
                .where(idField.eq(id))
                .execute()
        );
    }

    @Override
    public Single<List<P>> findAll() {
        return rxSchedulerIo(() -> getDSLContext().select()
                .from(getTable())
                .fetchInto(pojoClass));
    }

    @Override
    public Single<PageResponse<P>> findAll(PageRequest pageRequest) {
        int totalRecords = getTotalRecords();
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int totalPage = (int) Math.ceil(totalRecords * 1f / size);
        int offset = page * size;
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .offset(offset)
                .limit(size)
                .fetchInto(pojoClass)
        ).map(result -> PageResponse.<P>builder().data(result).page(page).size(size).totalPage(totalPage).build());
    }

    private Integer getTotalRecords() {
        return getDSLContext().selectCount()
                .from(getTable())
                .fetchOne(0, int.class);
    }

    @Override
    public Single<Optional<P>> findById(ID id) {
        return rxSchedulerIo(() -> ofNullable(getDSLContext().select()
                .from(getTable())
                .where(idField.eq(id))
                .fetchOneInto(pojoClass)
        ));
    }

    @Override
    public Single<Boolean> existsById(ID id) {
        return rxSchedulerIo(() -> getDSLContext().fetchExists(getTable(), idField.eq(id)));
    }

    @Override
    public Integer insertBlocking(P entity) {
        return 0;
    }

    @Override
    public P insertReturnBlocking(P entity) {
        return null;
    }

    @Override
    public Integer updateBlocking(ID id, P entity) {
        return 0;
    }

    @Override
    public P updateReturnBlocking(ID id, P entity) {
        return null;
    }

    @Override
    public Integer deleteByIdBlocking(ID id) {
        return 0;
    }

    @Override
    public List<P> findAllBlocking() {
        return List.of();
    }

    @Override
    public List<PageResponse<P>> findAllBlocking(PageRequest pageRequest) {
        return List.of();
    }

    @Override
    public Optional<P> findByIdBlocking(ID id) {
        return Optional.empty();
    }

    @Override
    public Boolean existsByIdBlocking(ID id) {
        return null;
    }
}
