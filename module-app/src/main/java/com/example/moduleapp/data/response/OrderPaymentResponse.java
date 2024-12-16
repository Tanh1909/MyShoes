package com.example.moduleapp.data.response;

import com.example.moduleapp.model.tables.pojos.OrderItem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class OrderPaymentResponse {
    private Integer id;
    private String status;
    private List<OrderItem> orderItems;
    private PaymentOrderResponse payment;
    private UserResponse user;
    private AddressResponse address;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

}
