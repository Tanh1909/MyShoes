package com.example.moduleapp.data.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {
    private Integer id;
    private UserResponse user;
    private List<OrderResponse> items;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private AddressResponse address;


    @Getter
    @Builder
    public static class UserResponse {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String address;
    }

    @Getter
    @Builder
    public static class OrderItemResponse {
        private String name;
        private Double price;
        private Integer quantity;
        private List<String> variants;
        private String imageUrl;
    }
}
