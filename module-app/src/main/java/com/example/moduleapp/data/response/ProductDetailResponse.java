package com.example.moduleapp.data.response;

import com.example.moduleapp.data.dto.ProductVariantDetail;
import com.example.moduleapp.model.tables.pojos.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProductDetailResponse {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Integer sold;
    private Double rating;
    private List<ImageResponse> images;
    private Integer categoryId;
    private List<ProductVariantDetail> productVariants;
}
