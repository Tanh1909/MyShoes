package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.Tables;
import com.example.moduleapp.model.tables.pojos.UserRole;
import com.example.repository.JooqRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRoleRepository extends JooqRepository<UserRole, Long> {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return Tables.USER_ROLE;
    }
}
