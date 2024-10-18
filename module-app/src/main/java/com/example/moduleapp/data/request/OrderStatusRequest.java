package com.example.moduleapp.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequest {
    private Integer orderId;
    private String status;
}
