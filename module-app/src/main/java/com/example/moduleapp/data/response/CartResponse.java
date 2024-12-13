package com.example.moduleapp.data.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CartResponse {
    private Integer id;
    private Integer quantity;
    private ProductVariant productVariant;
    private List<ProductVariant> options;

    public List<ProductVariant> getOptions() {
        return options == null ? Collections.emptyList() : options;
    }

    @Getter
    @Setter
    public static class ProductVariant {
        private Integer id;
        private Integer productId;
        private String name;
        private String imageUrl;
        private Double price;
        private Integer stock;
        private Map<String, String> attributes;


        public Double getPrice() {
            return price == null ? 0 : price;
        }

        public Map<String, String> getAttributes() {
            return attributes == null ? new HashMap<>() : attributes;
        }
    }
}
