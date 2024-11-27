package com.example.moduleapp.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ProductVariantDetail {
    private int id;
    private int productId;
    private Double price;
    private String skuCode;
    private Integer stock;
    private Map<String, String> attributes;
}
