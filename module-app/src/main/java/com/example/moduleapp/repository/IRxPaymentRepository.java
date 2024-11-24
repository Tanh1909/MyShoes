package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.Payment;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

public interface IRxPaymentRepository extends IRxJooqRepository<Payment, Integer> {

}
