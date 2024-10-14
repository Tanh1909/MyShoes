package com.example.moduleapp.data.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Integer addressId;
    private List<ProductVariantRequest> productVariants;

    @Getter
    @Setter
    public static class ProductVariantRequest {
        private Integer id;
        private Integer quantity;
    }
}
