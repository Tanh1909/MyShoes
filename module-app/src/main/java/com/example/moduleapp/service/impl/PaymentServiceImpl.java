package com.example.moduleapp.service.impl;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.AppException;
import com.example.moduleapp.config.constant.PaymentEnum;
import com.example.moduleapp.config.constant.PaymentErrorCode;
import com.example.moduleapp.config.constant.PaymentMethodEnum;
import com.example.moduleapp.config.constant.VNPayErrorCode;
import com.example.moduleapp.data.request.PaymentRequest;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.model.tables.pojos.Payment;
import com.example.moduleapp.payment.abstracts.PaymentAbstract;
import com.example.moduleapp.payment.factory.PaymentFactory;
import com.example.moduleapp.repository.impl.PaymentRepository;
import com.example.moduleapp.service.PaymentService;
import com.example.security.config.service.UserDetailImpl;
import com.example.security.service.AuthService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentFactory paymentFactory;
    private final AuthService authService;

    @Override
    public Single<PaymentResponse> pay(PaymentRequest paymentRequest) {
        UserDetailImpl userDetail = (UserDetailImpl) authService.getCurrentUser();
        PaymentMethodEnum paymentMethod = PaymentMethodEnum.getValue(paymentRequest.getPaymentMethod());
        if (paymentMethod == null) {
            throw new AppException(PaymentErrorCode.PAYMENT_METHOD_NOT_SUPPORT);
        }
        PaymentAbstract paymentAbstract = paymentFactory.create(paymentMethod);
        return paymentAbstract.pay(paymentRequest.getOrderId(), userDetail);
    }

    public Single<String> handleVNPayCallback(Integer paymentId, LocalDateTime paidAt, String responseCode) {
        switch (responseCode) {
            case "00" -> {
                return paymentRepository.findById(paymentId)
                        .flatMap(paymentOptional -> {
                            Payment payment = paymentOptional.orElse(null);
                            validatePayment(payment);
                            payment.setPaymentStatus(PaymentEnum.SUCCESS.getValue());
                            payment.setPaidAt(paidAt);
                            return paymentRepository.update(paymentId, payment);
                        }).map(integer -> "SUCCESS");
            }
            case "11" -> throw new AppException(VNPayErrorCode.TIME_OUT);
            case "13" -> throw new AppException(VNPayErrorCode.WRONG_OTP);
            default -> throw new AppException(VNPayErrorCode.NOT_SUCCESS);
        }

    }

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new AppException(ErrorCodeBase.NOT_FOUND, "PAYMENT ID");
        }
    }

}

