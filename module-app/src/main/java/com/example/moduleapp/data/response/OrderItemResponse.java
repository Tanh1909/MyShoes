package com.example.moduleapp.data.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class OrderItemResponse {
    private Integer id;
    private String name;
    private String imageUrl;
    private String code;
    private String status;
    private Integer quantity;
    private BigDecimal price;
    private Map<String, String> attributes;

    public Integer getQuantity() {
        return quantity == null ? 0 : quantity;
    }

    public BigDecimal getPrice() {
        return price == null ? BigDecimal.ZERO : price;
    }

    public Map<String, String> getAttributes() {
        return attributes == null ? new HashMap<>() : attributes;
    }
}
