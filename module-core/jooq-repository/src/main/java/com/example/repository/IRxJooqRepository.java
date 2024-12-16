package com.example.repository;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import io.reactivex.rxjava3.core.Single;
import org.jooq.Condition;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IRxJooqRepository<P, ID> {
    Single<Integer> insert(P entity);

    Single<List<Integer>> insert(Collection<P> entities);

    Single<P> insertReturn(P entity);

    Single<Optional<P>> insertIgnoreOnDuplicateKey(P pojo);

    Single<List<Integer>> insertIgnoreOnDuplicateKey(Collection<P> pojos);

    Single<Optional<P>> insertUpdateOnDuplicateKey(P pojo);

    Single<List<Integer>> insertUpdateOnDuplicateKey(Collection<P> pojos);

    Single<Integer> update(ID id, P entity);

    Single<P> updateReturn(ID id, P entity);

    Single<Integer> deleteById(ID id);

    Single<Integer> deleteByIds(Collection<ID> ids);

    Single<List<P>> findAll();

    Single<List<P>> findAllIgnoreFilter();

    Single<PageResponse<P>> findAll(PageRequest pageRequest);

    Single<PageResponse<P>> findAllByCondition(PageRequest pageRequest,Condition condition);

    Single<PageResponse<P>> findAllIgnoreFilter(PageRequest pageRequest);

    Single<Optional<P>> findById(ID id);

    Single<Optional<P>> findByIdIgnoreFilter(ID id);

    Single<List<P>> findByIds(Collection<ID> ids);

    Single<List<P>> findByIdsIgnoreFilter(Collection<ID> ids);

    Single<Boolean> existsById(ID id);

    Single<Boolean> existsByIdIgnoreFilter(ID id);

    Single<Integer> getTotalRecords();

    Single<Integer> getTotalRecords(Condition condition);

}
