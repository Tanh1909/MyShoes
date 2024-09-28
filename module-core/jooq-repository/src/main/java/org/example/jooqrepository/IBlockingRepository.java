package org.example.jooqrepository;

import org.example.common.data.request.PageRequest;
import org.example.common.data.response.PageResponse;

import java.util.List;
import java.util.Optional;

public interface IBlockingRepository<P, ID> {
    Integer insertBlocking(P entity);

    P insertReturnBlocking(P entity);

    Integer updateBlocking(ID id, P entity);

    P updateReturnBlocking(ID id, P entity);

    Integer deleteByIdBlocking(ID id);

    List<P> findAllBlocking();

    List<PageResponse<P>> findAllBlocking(PageRequest pageRequest);

    Optional<P> findByIdBlocking(ID id);

    Boolean existsByIdBlocking(ID id);
}
