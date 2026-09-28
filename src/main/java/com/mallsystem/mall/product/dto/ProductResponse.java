package com.mallsystem.mall.product.dto;

import com.mallsystem.mall.product.entity.ProductStatus;

import java.math.BigDecimal;

public record ProductResponse(
        String productCode,
        String name,
        String description,
        BigDecimal price,
        int stock,
        ProductStatus status) {
}
