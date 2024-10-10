package com.example.moduleapp.data.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Integer sold;
    private String imageUrl;
}
