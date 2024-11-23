package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.Address;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.Optional;

public interface IRxAddressRepository extends IRxJooqRepository<Address,Integer> {
    Single<Optional<Address>> findDefaultAddressByUserId(Long userId);
    Single<List<Address>> findByUserId(Long userId);
}
