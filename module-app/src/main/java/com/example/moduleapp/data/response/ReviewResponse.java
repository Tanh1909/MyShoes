package com.example.moduleapp.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponse {
    private Integer id;
    private Double rating;
    private String comment;
    private Product product;

    @Getter
    @Builder
    public static class Product {
        private Integer id;
        private String name;
        private Double price;
        private String image;
        private Map<String, String> variants;
    }

}
