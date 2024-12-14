package com.example.moduleapp.payment.concrete;

import com.example.common.context.UserPrincipal;
import com.example.common.utils.HashUtils;
import com.example.common.utils.TimeUtils;
import com.example.moduleapp.config.constant.PaymentMethodEnum;
import com.example.moduleapp.config.vnpay.VNPayConfigProperties;
import com.example.moduleapp.data.response.PaymentResponse;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.model.tables.pojos.Payment;
import com.example.moduleapp.payment.abstracts.PaymentAbstract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Component
@RequiredArgsConstructor
public class VNPAYPayment extends PaymentAbstract {
    private final VNPayConfigProperties vnPayConfigProperties;

    private static final String VNPAY_DATE_TIME_FORMAT = "yyyyMMddHHmmss";

    private static final String VNPAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?";

    @Override
    public PaymentMethodEnum getPaymentMethod() {
        return PaymentMethodEnum.VNPAY;
    }

    @Override
    public PaymentResponse handlePaymentResponse(Order order, UserPrincipal userPrincipal, Payment paymentResult) {
        Map<String, String> params = getVnPayConfig();
        params.put("vnp_OrderInfo", "THANH TOÁN CHO MÃ ĐƠN HÀNG: " + order.getId());
        params.put("vnp_IpAddr", userPrincipal.getClientId());
        params.put("vnp_Amount", String.valueOf(paymentResult.getAmount().setScale(0).multiply(new BigDecimal(100))));
        params.put("vnp_TxnRef", paymentResult.getId().toString());
        params.put("vnp_CreateDate", TimeUtils.getFormat(LocalDateTime.now(), VNPAY_DATE_TIME_FORMAT));
        params.put("vnp_ExpireDate", TimeUtils.getFormat(LocalDateTime.now().plusMinutes(vnPayConfigProperties.getExpirationTime() / (60 * 1000)), VNPAY_DATE_TIME_FORMAT));
        return PaymentResponse.builder()
                .success(Boolean.TRUE)
                .url(VNPAY_URL + buildQuery(params))
                .build();
    }

    public boolean verifyPayment(Map<String, String> params) {
        String secureHash = params.remove("vnp_SecureHash");

        if (secureHash == null || !"00".equals(params.get("vnp_ResponseCode"))) {
            return false;
        }
        String generateSecureHash = generateSecureHash(buildParams(params,true));
        return secureHash.equals(generateSecureHash);
    }


    private String buildQuery(Map<String, String> params) {
        String buildParams = buildParams(params,true);
        return buildParams +
                "&vnp_SecureHash=" +
                generateSecureHash(buildParams);
    }

    private String buildParams(Map<String, String> params,boolean isEncode) {
        StringJoiner stringJoiner = new StringJoiner("&");
        params.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getKey() != null)
                .filter(entry -> !entry.getKey().equals("vnp_SecretKey"))
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String value = isEncode?URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8):entry.getValue();
                    stringJoiner.add(entry.getKey() + "=" + value);
                });
        return stringJoiner.toString();
    }

    private String generateSecureHash(String params) {
        return HashUtils.hmacSHA512(vnPayConfigProperties.getHashSecret(), params);
    }

    private Map<String, String> getVnPayConfig() {
        Map<String, String> map = new HashMap<>();
        map.put("vnp_Version", vnPayConfigProperties.getVersion());
        map.put("vnp_Command", vnPayConfigProperties.getCommand());
        map.put("vnp_TmnCode", vnPayConfigProperties.getTmnCode());
        map.put("vnp_CurrCode", "VND");
        map.put("vnp_OrderType", vnPayConfigProperties.getOrderType());
        map.put("vnp_Locale", "vn");
        map.put("vnp_ReturnUrl", vnPayConfigProperties.getReturnUrl());
        map.put("vnp_SecretKey", vnPayConfigProperties.getHashSecret());
        return map;
    }
}
