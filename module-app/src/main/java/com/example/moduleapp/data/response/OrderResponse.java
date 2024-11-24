package com.example.moduleapp.data.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Integer id;
    private String status;
    private List<OrderItemResponse> items;
    private AddressResponse shippingAddress;
    private Double totalAmount;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    public static class AddressResponse {
        private Integer id;
        private String content;
        private String phone;
    }

}
