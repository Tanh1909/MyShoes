package com.example.moduleapp.data.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OrderCreateResponse {
    private Integer orderId;
}
