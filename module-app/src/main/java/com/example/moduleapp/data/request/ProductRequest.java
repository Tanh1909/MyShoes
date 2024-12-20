package com.example.moduleapp.data.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer categoryId;
    private List<ImageRequest> images;
    private Set<AttributeRequest> attributes;
    private List<VariantsRequest> variants;


    @Getter
    @Setter
    public static class AttributeRequest {
        private String name;
        private Set<String> options;
    }

    @Getter
    @Setter
    public static class VariantsRequest {
        private String skuCode;
        private Double price;
        private Integer stock;
        private List<String> attributeOptions;

    }


}
