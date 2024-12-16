package com.example.moduleapp.data.request;

import com.example.common.data.request.pagination.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPaymentRequest extends PageRequest {
    private String username;
    private String status;
}
