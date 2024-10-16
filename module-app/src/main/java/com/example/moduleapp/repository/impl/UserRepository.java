package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.Tables;
import com.example.moduleapp.model.tables.pojos.User;
import com.example.moduleapp.repository.IRxUserRepository;
import com.example.repository.JooqRepository;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.*;
import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class UserRepository extends JooqRepository<User, Integer> implements IRxUserRepository {
    private final DSLContext dsl;

    @Override
    protected DSLContext getDSLContext() {
        return dsl;
    }

    @Override
    protected Table getTable() {
        return USER;
    }

    @Override
    public Single<Optional<User>> findByUsername(String username) {
        return rxSchedulerIo(() -> ofNullable(getDSLContext().select()
                .from(getTable())
                .where(USER.USERNAME.eq(username))
                .fetchOneInto(pojoClass)));
    }

    @Override
    public Single<Boolean> existByUsername(String username) {
        return rxSchedulerIo(() -> getDSLContext().fetchExists(getTable(), USER.USERNAME.eq(username)));
    }
}
