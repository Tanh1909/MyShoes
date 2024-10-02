package com.example.security.repository;

import com.example.security.model.tables.pojos.User;
import io.reactivex.rxjava3.core.Single;

import java.util.Optional;

public interface IRxUserRepository {
    Single<Optional<User>> findByUsername(String username);

    Single<Boolean> existByUsername(String username);
}
