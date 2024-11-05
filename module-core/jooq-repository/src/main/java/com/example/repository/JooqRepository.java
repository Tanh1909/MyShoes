package com.example.repository;

import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.repository.utils.SQLQueryUtils;
import io.reactivex.rxjava3.core.Single;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.jooq.*;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static java.util.Optional.ofNullable;

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
    public Single<List<Integer>> insert(Collection<P> entities) {
        List<InsertSetMoreStep> result = entities.stream()
                .map(p -> SQLQueryUtils.toInsertQueries(getTable(), p))
                .map(fieldObjectMap -> getDSLContext().insertInto(getTable())
                        .set(fieldObjectMap)).toList();
        return rxSchedulerIo(() -> Arrays.stream(getDSLContext()
                        .batch(result)
                        .execute())
                .boxed()
                .toList());
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
    public Single<Optional<P>> insertIgnoreOnDuplicateKey(P pojo) {
        return rxSchedulerIo(() -> ofNullable(getDSLContext()
                .insertInto(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), pojo))
                .onDuplicateKeyIgnore()
                .returning()
                .fetchOne()
                .map(record -> record.into(pojoClass))
        ));
    }

    @Override
    public Single<List<Integer>> insertIgnoreOnDuplicateKey(Collection<P> pojos) {
        List<InsertReturningStep> result = pojos.stream()
                .map(p -> SQLQueryUtils.toInsertQueries(getTable(), p))
                .map(fieldObjectMap -> getDSLContext()
                        .insertInto(getTable())
                        .set(fieldObjectMap)
                        .onDuplicateKeyIgnore()
                ).toList();
        return rxSchedulerIo(() -> Arrays.stream(getDSLContext()
                        .batch(result)
                        .execute())
                .boxed()
                .toList());
    }

    @Override
    public Single<Optional<P>> insertUpdateOnDuplicateKey(P pojo) {
        return rxSchedulerIo(() -> ofNullable(getDSLContext()
                .insertInto(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), pojo))
                .onDuplicateKeyUpdate()
                .set(SQLQueryUtils.toInsertQueries(getTable(), pojo))
                .returning()
                .fetchOne()
                .map(record -> record.into(pojoClass))
        ));
    }

    @Override
    public Single<List<Integer>> insertUpdateOnDuplicateKey(Collection<P> pojos) {
        List<InsertOnDuplicateSetMoreStep> result = pojos.stream()
                .map(p -> SQLQueryUtils.toInsertQueries(getTable(), p))
                .map(fieldObjectMap -> getDSLContext()
                        .insertInto(getTable())
                        .set(fieldObjectMap)
                        .onDuplicateKeyUpdate()
                        .set(fieldObjectMap)
                ).toList();
        return rxSchedulerIo(() -> Arrays.stream(getDSLContext()
                        .batch(result)
                        .execute())
                .boxed()
                .toList());
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

    protected Integer getTotalRecords() {
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
    public Single<List<P>> findByIds(Collection<ID> ids) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(idField.in(ids))
                .fetchInto(pojoClass)
        );
    }

    @Override
    public Single<Boolean> existsById(ID id) {
        return rxSchedulerIo(() -> getDSLContext().fetchExists(getTable(), idField.eq(id)));
    }

    @Override
    public Integer insertBlocking(P entity) {
        return getDSLContext().insertInto(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), entity))
                .execute();
    }

    @Override
    public List<Integer> insertBlocking(Collection<P> entities) {
        List<InsertSetMoreStep> result = entities.stream()
                .map(p -> SQLQueryUtils.toInsertQueries(getTable(), p))
                .map(fieldObjectMap -> getDSLContext().insertInto(getTable())
                        .set(fieldObjectMap)).toList();
        return Arrays.stream(getDSLContext()
                        .batch(result)
                        .execute())
                .boxed()
                .toList();
    }

    @Override
    public P insertReturnBlocking(P entity) {
        return getDSLContext().insertInto(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), entity))
                .returning()
                .fetchOne()
                .map(record -> record.into(pojoClass));
    }

    @Override
    public List<P> insertReturnBlocking(Collection<P> entities) {
        return entities.stream().map(this::insertReturnBlocking).toList();
    }

    @Override
    public Optional<P> insertIgnoreOnDuplicateKeyBlocking(P pojo) {
        return ofNullable(getDSLContext()
                .insertInto(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), pojo))
                .onDuplicateKeyIgnore()
                .returning()
                .fetchOne()
                .map(record -> record.into(pojoClass)));
    }

    @Override
    public List<Integer> insertIgnoreOnDuplicateKeyBlocking(Collection<P> pojos) {
        List<InsertReturningStep> result = pojos.stream()
                .map(p -> SQLQueryUtils.toInsertQueries(getTable(), p))
                .map(fieldObjectMap -> getDSLContext()
                        .insertInto(getTable())
                        .set(fieldObjectMap)
                        .onDuplicateKeyIgnore()
                ).toList();
        return Arrays.stream(getDSLContext()
                        .batch(result)
                        .execute())
                .boxed()
                .toList();
    }

    @Override
    public Optional<P> insertUpdateOnDuplicateKeyBlocking(P pojo) {
        return ofNullable(getDSLContext()
                .insertInto(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), pojo))
                .onDuplicateKeyUpdate()
                .set(SQLQueryUtils.toInsertQueries(getTable(), pojo))
                .returning()
                .fetchOne()
                .map(record -> record.into(pojoClass)));
    }

    @Override
    public List<Integer> insertUpdateOnDuplicateKeyBlocking(Collection<P> pojos) {
        List<InsertOnDuplicateSetMoreStep> result = pojos.stream()
                .map(p -> SQLQueryUtils.toInsertQueries(getTable(), p))
                .map(fieldObjectMap -> getDSLContext()
                        .insertInto(getTable())
                        .set(fieldObjectMap)
                        .onDuplicateKeyUpdate()
                        .set(fieldObjectMap)
                ).toList();
        return Arrays.stream(getDSLContext()
                        .batch(result)
                        .execute())
                .boxed()
                .toList();
    }

    @Override
    public Integer updateBlocking(ID id, P entity) {
        return getDSLContext().update(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), entity))
                .where(idField.eq(id))
                .execute();
    }

    @Override
    public P updateReturnBlocking(ID id, P entity) {
        return getDSLContext().update(getTable())
                .set(SQLQueryUtils.toInsertQueries(getTable(), entity))
                .where(idField.eq(id))
                .returning()
                .fetchOne()
                .map(record -> record.into(pojoClass));
    }

    @Override
    public Integer deleteByIdBlocking(ID id) {
        return getDSLContext().delete(getTable())
                .where(idField.eq(id))
                .execute();
    }

    @Override
    public List<P> findAllBlocking() {
        return getDSLContext().select()
                .from(getTable())
                .fetchInto(pojoClass);
    }

    @Override
    public PageResponse<P> findAllBlocking(PageRequest pageRequest) {
        int totalRecords = getTotalRecords();
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int totalPage = (int) Math.ceil(totalRecords * 1f / size);
        int offset = page * size;
        List<P> results = getDSLContext()
                .select()
                .from(getTable())
                .offset(offset)
                .limit(size)
                .fetchInto(pojoClass);
        return PageResponse.<P>builder().data(results).page(page).size(size).totalPage(totalPage).build();
    }

    @Override
    public Optional<P> findByIdBlocking(ID id) {
        return ofNullable(getDSLContext().select()
                .from(getTable())
                .where(idField.eq(id))
                .fetchOneInto(pojoClass));
    }

    @Override
    public Boolean existsByIdBlocking(ID id) {
        return getDSLContext().fetchExists(getTable(), idField.eq(id));
    }
}
