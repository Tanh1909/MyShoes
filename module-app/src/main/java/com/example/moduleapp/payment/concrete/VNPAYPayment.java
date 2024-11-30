package com.example.moduleapp.payment.concrete;

import com.example.common.context.UserPrincipal;
import com.example.common.utils.HashUtils;
import com.example.moduleapp.config.constant.PaymentMethodEnum;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.model.tables.pojos.Payment;
import com.example.moduleapp.payment.abstracts.PaymentAbstract;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
    public PaymentResponse handlePaymentResponse(Order order, UserPrincipal userPrincipal, Payment paymentResult) {
        Map<String, String> params = vnPayConfig;
        params.put("vnp_OrderInfo", "THANH TOÁN CHO MÃ ĐƠN HÀNG: " + order.getId());
        params.put("vnp_IpAddr", userPrincipal.getClientId());
        params.put("vnp_Amount", String.valueOf(paymentResult.getAmount().setScale(0).multiply(new BigDecimal(100))));
        params.put("vnp_TxnRef", paymentResult.getId().toString());
        return PaymentResponse.builder()
                .success(Boolean.TRUE)
                .url(VNPAY_URL + buildQuery(params))
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
