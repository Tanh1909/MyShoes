package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.Address;
import com.example.moduleapp.repository.IRxAddressRepository;
import com.example.repository.JooqRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

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
}
