package com.example.moduleapp.payment.abstracts;

import com.example.common.context.UserPrincipal;
import com.example.common.exception.AppException;
import com.example.moduleapp.config.constant.*;
import com.example.moduleapp.data.mapper.PaymentMapper;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.model.tables.pojos.Payment;
import com.example.moduleapp.model.tables.pojos.PaymentMethod;
import com.example.moduleapp.repository.impl.OrderItemRepository;
import com.example.moduleapp.repository.impl.OrderRepository;
import com.example.moduleapp.repository.impl.PaymentMethodRepository;
import com.example.moduleapp.repository.impl.PaymentRepository;
import io.reactivex.rxjava3.core.Single;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class PaymentAbstract {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private PaymentMapper paymentMapper;

    public abstract PaymentMethodEnum getPaymentMethod();


    public Single<PaymentResponse> pay(Integer orderId, UserPrincipal userPrincipal) {
        return Single.zip(
                        paymentRepository.findByOrderId(orderId),
                        orderRepository.findById(orderId),
                        orderItemRepository.findByOrderId(orderId),
                        paymentMethodRepository.findByName(getPaymentMethod().getValue()),
                        (paymentOptional, orderOptional, orderItems, paymentMethodOptional) -> {
                            Order order = orderOptional
                                    .orElseThrow(() -> new AppException(AppErrorCode.NOT_FOUND, "ORDER ID"));
                            PaymentMethod paymentMethod = paymentMethodOptional
                                    .orElseThrow(() -> new AppException(AppErrorCode.IS_NOT_SUPPORTED, "PAYMENT METHOD"));
                            if (!OrderEnum.PENDING.getValue().equals(order.getStatus())) {
                                throw new AppException(AppErrorCode.ORDER_HAS_BEEN_PAYED);
                            }
                            BigDecimal totalAmount = orderItems.stream()
                                    .filter(orderItem -> OrderItemEnum.SUCCESS.getValue().equals(orderItem.getStatus()))
                                    .map(orderItem -> BigDecimal.valueOf(orderItem.getQuantity()).multiply(orderItem.getPrice()))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            Payment payment = new Payment();
                            payment.setOrderId(order.getId());
                            payment.setPaymentMethodId(paymentMethod.getId());
                            payment.setAmount(totalAmount);
                            payment.setPaymentStatus(PaymentEnum.PENDING.getValue());
                            if (paymentOptional.isPresent()) {
                                Payment paymentSql = paymentOptional.get();
                                paymentMapper.toPayment(paymentSql, payment);
                                return Single.just(handlePaymentResponse(order, userPrincipal, paymentSql));
                            }
                            return paymentRepository.insertReturn(payment)
                                    .map(paymentResult -> handlePaymentResponse(order, userPrincipal, paymentResult));
                        })
                .flatMap(p -> p);
    }


    public abstract PaymentResponse handlePaymentResponse(Order order, UserPrincipal userPrincipal, Payment paymentResult);

}
