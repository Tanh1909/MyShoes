package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.PaymentMethod;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Optional;

public interface IRxPaymentMethodRepository extends IRxJooqRepository<PaymentMethod, Integer> {
    Single<Optional<PaymentMethod>> findByName(String name);
}
