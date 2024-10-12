package com.example.moduleapp.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    private boolean success;
    private String transactionId;
    private String url;
    private String message;
}
