package com.example.security.repository.impl;

import com.example.repository.JooqRepository;
import com.example.security.model.tables.pojos.Role;
import com.example.security.repository.IRxRoleRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.security.model.Tables.*;

@Repository
@RequiredArgsConstructor
public class RoleRepository extends JooqRepository<Role, Integer> implements IRxRoleRepository {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return ROLE;
    }

    @Override
    public Single<List<Role>> findByUsername(String username) {
        return rxSchedulerIo(() -> getDSLContext().select()
                .from(getTable())
                .leftJoin(USER_ROLE).on(ROLE.ID.eq(USER_ROLE.ROLE_ID))
                .leftJoin(USER).on(USER_ROLE.USER_ID.eq(USER.ID))
                .fetchInto(pojoClass)
        );
    }
}
