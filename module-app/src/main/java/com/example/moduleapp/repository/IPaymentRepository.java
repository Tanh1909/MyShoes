package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.Payment;
import com.example.repository.IBlockingRepository;

public interface IPaymentRepository extends IBlockingRepository<Payment, Integer> {
    Boolean findPaymentSuccessBlocking(Integer orderId);

}
