package com.example.moduleapp.service.impl;

import com.example.common.context.SecurityContext;
import com.example.common.context.UserPrincipal;
import com.example.common.exception.AppException;
import com.example.common.utils.JsonUtils;
import com.example.moduleapp.config.constant.*;
import com.example.moduleapp.config.vnpay.VNPayReturnParams;
import com.example.moduleapp.data.request.PaymentRequest;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.model.tables.pojos.Payment;
import com.example.moduleapp.payment.abstracts.PaymentAbstract;
import com.example.moduleapp.payment.concrete.VNPAYPayment;
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
import java.util.Map;

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
    public Single<String> verify(Map<String,String> vnPayReturnParams) {
        VNPAYPayment paymentAbstract = (VNPAYPayment) paymentFactory.create(PaymentMethodEnum.VNPAY);
        boolean isVerify = paymentAbstract.verifyPayment(vnPayReturnParams);
        if (isVerify) {
            Integer paymentId = Integer.valueOf(vnPayReturnParams.get("vnp_TxnRef"));
            return paymentRepository.findById(paymentId)
                    .flatMap(paymentOptional -> {
                        Payment payment = paymentOptional.orElseThrow(() -> new AppException(AppErrorCode.NOT_FOUND, "PAYMENT ID"));
                        payment.setStatus(PaymentEnum.SUCCESS.getValue());
                        payment.setPaidAt(LocalDateTime.now());
                        return orderRepository.findById(payment.getOrderId())
                                .map(orderOptional -> {
                                    Order order = getOptionalValue(orderOptional, Order.class);
                                    order.setStatus(OrderEnum.PAYMENT_CONFIRMED.getValue());
                                    orderRepository.updateBlocking(order.getId(), order);
                                    paymentRepository.updateBlocking(paymentId, payment);
                                    return orderOptional;
                                });
                    }).map(integer -> "SUCCESS");
        }
        throw new AppException(VNPayErrorCode.NOT_SUCCESS);
    }


}

