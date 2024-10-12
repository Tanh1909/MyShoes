package com.example.moduleapp.payment.concrete;

import com.example.common.utils.HashUtils;
import com.example.moduleapp.config.constant.PaymentEnum;
import com.example.moduleapp.config.constant.PaymentMethodEnum;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.payment.abstracts.PaymentAbstract;
import com.example.security.config.service.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

@Component
@RequiredArgsConstructor
public class VNPAYPayment extends PaymentAbstract {
    @Qualifier("vnpayConfig")
    private final Map<String, String> vnPayConfig;

    private static final String VNPAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?";

    @Override
    public PaymentMethodEnum getPaymentMethod() {
        return PaymentMethodEnum.VNPAY;
    }

    @Override
    public PaymentResponse pay(String orderId, double amount, UserDetails userDetails) {
        UserDetailImpl userDetail = (UserDetailImpl) userDetails;
        Map<String, String> params = vnPayConfig;
        params.put("vnp_IpAddr", userDetail.getIpAddress());
        params.put("vnp_Amount", String.valueOf(100000));
        params.put("vnp_TxnRef", orderId);
        params.put("vnp_OrderInfo", "THANH TOÁN CHO MÃ ĐƠN HÀNG: " + orderId);
        return PaymentResponse.builder()
                .success(Boolean.TRUE)
                .url(VNPAY_URL + buildQuery(params))
                .message(PaymentEnum.PENDING.getValue())
                .build();
    }

    private String buildQuery(Map<String, String> params) {
        StringJoiner stringJoiner = new StringJoiner("&");
        params.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getKey() != null)
                .filter(entry -> !entry.getKey().equals("vnp_SecretKey"))
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String value = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
                    stringJoiner.add(entry.getKey() + "=" + value);
                });
        String secureHash = HashUtils.hmacSHA512(vnPayConfig.get("vnp_SecretKey"), stringJoiner.toString());
        stringJoiner.add("vnp_SecureHash" + "=" + URLEncoder.encode(secureHash, StandardCharsets.UTF_8));
        return stringJoiner.toString();
    }
}
