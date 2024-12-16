package com.example.moduleapp.data.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentOrderResponse {
    private Integer id;
    private String paymentMethod;
    private String amount;
    private String status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
