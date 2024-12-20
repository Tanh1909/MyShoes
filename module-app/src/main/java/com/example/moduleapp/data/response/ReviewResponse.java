package com.example.moduleapp.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponse {
    private Integer id;
    private Double rating;
    private String comment;
    private ProductVariant productVariant;
    private List<String> imageUrls;
    private UserResponse user;
    private LocalDateTime reviewedAt;

    @Getter
    @Builder
    public static class ProductVariant {
        private Integer id;
        private Integer productId;
        private String name;
        private Double price;
        private String image;
        private Map<String, String> variants;
    }

    public List<String> getImageUrls() {
        return imageUrls == null ? Collections.emptyList() : imageUrls;
    }
}
