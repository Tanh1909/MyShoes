package com.example.moduleapp.repository;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.moduleapp.model.tables.pojos.Cart;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

public interface IRxCartRepository extends IRxJooqRepository<Cart,Long> {
    Single<PageResponse<Cart>> findByUserId(Long userId, PageRequest pageRequest);
}
