package com.example.moduleapp.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartUpdateRequest {
    private Integer productVariantId;
    private Integer quantity;
}
