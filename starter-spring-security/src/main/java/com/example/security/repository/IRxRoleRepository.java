package com.example.security.repository;

import com.example.security.model.tables.pojos.Role;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.Optional;

public interface IRxRoleRepository {
    Single<List<Role>> findByUsername(String username);
    Single<Optional<Role>> findByRoleName(String name);
}
