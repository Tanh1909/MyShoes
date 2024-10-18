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
    private String description;
    private Double price;
    private Integer sold;
    private String imageUrl;

    public Double getPrice() {
        return price == null ? 0 : price;
    }

    public Integer getSold() {
        return sold == null ? 0 : sold;
    }
}
