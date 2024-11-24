package com.example.moduleapp.data.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private Double price;
    private Integer sold;
    private Double rating;
    private String imageUrl;
    private Integer categoryId;

    public Double getPrice() {
        return price == null ? 0 : price;
    }

    public Integer getSold() {
        return sold == null ? 0 : sold;
    }
}
