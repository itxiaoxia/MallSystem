package com.mallsystem.mall.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productCode,
        String productName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineAmount) {
}
