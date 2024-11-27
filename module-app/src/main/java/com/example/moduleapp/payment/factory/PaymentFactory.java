package com.example.moduleapp.payment.factory;

import com.cloudinary.api.exceptions.ApiException;
import com.example.moduleapp.config.constant.AppErrorCode;
import com.example.moduleapp.config.constant.PaymentMethodEnum;
import com.example.moduleapp.payment.abstracts.PaymentAbstract;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentFactory {
    public final List<PaymentAbstract> paymentAbstracts;

    @SneakyThrows
    public PaymentAbstract create(PaymentMethodEnum paymentMethodEnum) {
        for (PaymentAbstract paymentAbstract : paymentAbstracts) {
            if (paymentMethodEnum.equals(paymentAbstract.getPaymentMethod())) {
                return paymentAbstract;
            }
        }
        throw new ApiException(AppErrorCode.PAYMENT_METHOD_NOT_SUPPORT.getMessage());
    }
}
