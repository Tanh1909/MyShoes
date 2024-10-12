package com.example.moduleapp.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private String paymentMethod;
    private Integer orderId;
}
