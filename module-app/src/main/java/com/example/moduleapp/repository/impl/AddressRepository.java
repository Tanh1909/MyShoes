package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.Address;
import com.example.moduleapp.repository.IRxAddressRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.ADDRESS;

@Repository
@RequiredArgsConstructor
public class AddressRepository extends JooqRepository<Address, Integer> implements IRxAddressRepository {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return ADDRESS;
    }

    @Override
    public Single<Optional<Address>> findDefaultAddressByUserId(Long userId) {

        return rxSchedulerIo(() -> Optional.ofNullable(
                getDSLContext()
                        .select()
                        .from(getTable())
                        .where(ADDRESS.USER_ID.eq(userId).and(ADDRESS.IS_DEFAULT.isTrue()))
                        .fetchOneInto(pojoClass))
        );
    }

    @Override
    public Single<List<Address>> findByUserId(Long userId) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(ADDRESS.USER_ID.eq(userId))
                .fetchInto(pojoClass));
    }
}
