package com.example.repository;

import io.reactivex.rxjava3.core.Single;
import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;

import java.util.List;
import java.util.Optional;

public interface IRxJooqRepository<P, ID> {
    Single<Integer> insert(P entity);

    Single<P> insertReturn(P entity);

    Single<Integer> update(ID id, P entity);

    Single<P> updateReturn(ID id, P entity);

    Single<Integer> deleteById(ID id);

    Single<List<P>> findAll();

    Single<PageResponse<P>> findAll(PageRequest pageRequest);

    Single<Optional<P>> findById(ID id);

    Single<Boolean> existsById(ID id);

}