package com.example.security.repository.impl;

import com.example.repository.JooqRepository;
import com.example.security.model.tables.pojos.UserRole;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import static com.example.security.model.Tables.USER_ROLE;

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
        return USER_ROLE;
    }
}
