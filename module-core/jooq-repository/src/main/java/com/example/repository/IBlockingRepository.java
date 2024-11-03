package com.example.repository;

import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IBlockingRepository<P, ID> {
    Integer insertBlocking(P entity);

    List<Integer> insertBlocking(Collection<P> entities);

    P insertReturnBlocking(P entity);

    List<P> insertReturnBlocking(Collection<P> entities);

    Optional<P> insertIgnoreOnDuplicateKeyBlocking(P pojo);

    List<Integer> insertIgnoreOnDuplicateKeyBlocking(Collection<P> pojos);

    Optional<P> insertUpdateOnDuplicateKeyBlocking(P pojo);

    List<Integer> insertUpdateOnDuplicateKeyBlocking(Collection<P> pojos);

    Integer updateBlocking(ID id, P entity);

    P updateReturnBlocking(ID id, P entity);

    Integer deleteByIdBlocking(ID id);

    List<P> findAllBlocking();

    PageResponse<P> findAllBlocking(PageRequest pageRequest);

    Optional<P> findByIdBlocking(ID id);

    Boolean existsByIdBlocking(ID id);
}
