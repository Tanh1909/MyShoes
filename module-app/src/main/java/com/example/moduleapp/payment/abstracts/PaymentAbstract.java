package com.example.moduleapp.payment.abstracts;

import com.example.common.context.UserPrincipal;
import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.AppException;
import com.example.moduleapp.config.constant.OrderEnum;
import com.example.moduleapp.config.constant.PaymentEnum;
import com.example.moduleapp.config.constant.PaymentErrorCode;
import com.example.moduleapp.config.constant.PaymentMethodEnum;
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

    public abstract PaymentMethodEnum getPaymentMethod();


    public Single<PaymentResponse> pay(Integer orderId, UserPrincipal userPrincipal) {
        return Single.zip(
                        orderRepository.findById(orderId),
                        orderItemRepository.findByOrderId(orderId),
                        paymentMethodRepository.findByName(getPaymentMethod().getValue()),
                        (orderOptional, orderItems, paymentMethodOptional) -> {
                            Order order = orderOptional.orElse(null);
                            PaymentMethod paymentMethod = paymentMethodOptional.orElse(null);
                            validatePaymentMethod(paymentMethod);
                            validateOrder(order);
                            BigDecimal totalAmount = orderItems.stream()
                                    .map(orderItem -> BigDecimal.valueOf(orderItem.getQuantity()).multiply(orderItem.getPrice()))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            Payment payment = new Payment();
                            payment.setOrderId(order.getId());
                            payment.setPaymentMethodId(paymentMethod.getId());
                            payment.setAmount(totalAmount);
                            payment.setPaymentStatus(PaymentEnum.PENDING.getValue());
                            return paymentRepository.insertReturn(payment)
                                    .map(paymentResult -> handlePaymentResponse(order, userPrincipal, paymentResult, totalAmount));
                        })
                .flatMap(p -> p);
    }


    public abstract PaymentResponse handlePaymentResponse(Order order, UserPrincipal userPrincipal, Payment paymentResult, BigDecimal totalAmount);


    private static void validatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new AppException(ErrorCodeBase.IS_NOT_SUPPORTED, "PAYMENT METHOD");
        }
    }

    private static void validateOrder(Order order) {
        if (order == null) {
            throw new AppException(ErrorCodeBase.NOT_FOUND, "ORDER ID");
        }
        if (!OrderEnum.PENDING.getValue().equals(order.getStatus())) {
            throw new AppException(PaymentErrorCode.ORDER_HAS_BEEN_PAYED);
        }
    }


}
