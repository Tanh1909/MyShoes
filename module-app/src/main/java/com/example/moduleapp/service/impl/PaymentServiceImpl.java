package com.example.moduleapp.service.impl;

import com.example.common.context.SecurityContext;
import com.example.common.context.UserPrincipal;
import com.example.common.exception.AppException;
import com.example.moduleapp.config.constant.*;
import com.example.moduleapp.data.request.PaymentRequest;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.model.tables.pojos.Payment;
import com.example.moduleapp.payment.abstracts.PaymentAbstract;
import com.example.moduleapp.payment.factory.PaymentFactory;
import com.example.moduleapp.repository.impl.OrderRepository;
import com.example.moduleapp.repository.impl.PaymentRepository;
import com.example.moduleapp.service.AuthService;
import com.example.moduleapp.service.PaymentService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.common.utils.ValidateUtils.getOptionalValue;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentFactory paymentFactory;
    private final OrderRepository orderRepository;
    private final AuthService authService;

    @Override
    public Single<PaymentResponse> pay(PaymentRequest paymentRequest) {
        UserPrincipal userPrincipal = SecurityContext.getUserPrincipal();
        PaymentMethodEnum paymentMethod = PaymentMethodEnum.getValue(paymentRequest.getPaymentMethod());
        if (paymentMethod == null) {
            throw new AppException(AppErrorCode.PAYMENT_METHOD_NOT_SUPPORT);
        }
        PaymentAbstract paymentAbstract = paymentFactory.create(paymentMethod);
        return paymentAbstract.pay(paymentRequest.getOrderId(), userPrincipal);
    }

    @Transactional
    public Single<String> handleVNPayCallback(Integer paymentId, LocalDateTime paidAt, String responseCode) {
        switch (responseCode) {
            case "00" -> {
                return paymentRepository.findById(paymentId)
                        .flatMap(paymentOptional -> {
                            Payment payment = getOptionalValue(paymentOptional, Payment.class);
                            payment.setPaymentStatus(PaymentEnum.SUCCESS.getValue());
                            payment.setPaidAt(paidAt);
                            return orderRepository.findById(payment.getOrderId())
                                    .map(orderOptional -> {
                                        Order order = getOptionalValue(orderOptional, Order.class);
                                        order.setStatus(OrderEnum.PAYMENT_CONFIRM.getValue());
                                        orderRepository.updateBlocking(order.getId(), order);
                                        paymentRepository.updateBlocking(paymentId, payment);
                                        return orderOptional;
                                    });
                        }).map(integer -> "SUCCESS");
            }
            case "11" -> throw new AppException(VNPayErrorCode.TIME_OUT);
            case "13" -> throw new AppException(VNPayErrorCode.WRONG_OTP);
            default -> throw new AppException(VNPayErrorCode.NOT_SUCCESS);
        }

    }


}

