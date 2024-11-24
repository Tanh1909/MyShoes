package com.example.moduleapp.data.response;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class OrderItemResponse {
    private Integer id;
    private String name;
    private String imageUrl;
    private Integer quantity;
    private Double price;
    private Map<String, String> attributes;

    public Integer getQuantity() {
        return quantity == null ? 0 : quantity;
    }

    public Double getPrice() {
        return price == null ? 0 : price;
    }

    public Map<String, String> getAttributes() {
        return attributes == null ? new HashMap<>() : attributes;
    }
}
