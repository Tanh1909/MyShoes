package com.example.moduleapp.data.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private List<Integer> productVariantIds;
}
