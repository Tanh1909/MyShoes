package com.example.moduleapp.data.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class RemoveCartRequest {
    private long userId;
    private List<Integer> productVariantIds;
}
