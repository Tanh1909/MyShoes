package com.example.moduleapp.config.vnpay;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VNPayReturnParams {
    private String vnp_TmnCode;
    private BigDecimal vnp_Amount;
    private String vnp_BankCode;
    private String vnp_OrderInfo;
    private String vnp_TransactionNo;
    private String vnp_ResponseCode;
    private String vnp_TransactionStatus;
    private String vnp_TxnRef;
    private String vnp_SecureHash;
}
