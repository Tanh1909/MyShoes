package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.Role;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.Optional;

public interface IRxRoleRepository extends IRxJooqRepository<Role, Integer> {
    Single<List<Role>> findByUsername(String username);

    Single<Optional<Role>> findByRoleName(String name);
}
