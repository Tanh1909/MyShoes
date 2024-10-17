package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.User;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Optional;

public interface IRxUserRepository extends IRxJooqRepository<User, Integer> {
    Single<Optional<User>> findByUsername(String username);

    Single<Boolean> existByUsername(String username);
}
