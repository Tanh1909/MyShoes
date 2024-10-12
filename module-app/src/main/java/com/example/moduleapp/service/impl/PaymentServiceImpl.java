package com.example.moduleapp.service.impl;

import com.example.common.exception.AppException;
import com.example.moduleapp.config.constant.PaymentErrorCode;
import com.example.moduleapp.config.constant.PaymentMethodEnum;
import com.example.moduleapp.data.request.PaymentRequest;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.payment.abstracts.PaymentAbstract;
import com.example.moduleapp.payment.factory.PaymentFactory;
import com.example.moduleapp.repository.impl.PaymentRepository;
import com.example.moduleapp.service.PaymentService;
import com.example.security.config.service.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentFactory paymentFactory;

    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest) {
        UserDetailImpl userDetail = new UserDetailImpl();
        userDetail.setIpAddress("127.0.0.1");
        PaymentMethodEnum paymentMethod = PaymentMethodEnum.getValue(paymentRequest.getPaymentMethod());
        if (paymentMethod == null) {
            throw new AppException(PaymentErrorCode.PAYMENT_METHOD_NOT_SUPPORT);
        }
        PaymentAbstract paymentAbstract = paymentFactory.create(paymentMethod);
        return paymentAbstract.pay("1213", 120000, userDetail);
    }
}
