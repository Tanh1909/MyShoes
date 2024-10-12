package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.PaymentMethod;
import com.example.repository.IRxJooqRepository;

public interface IRxPaymentMethodRepository extends IRxJooqRepository<PaymentMethod, Integer> {
}
