package com.mallsystem.mall.product.dto;

import com.mallsystem.mall.product.entity.ProductStatus;

public record InventoryResponse(String productCode, String name, int stock, ProductStatus status) {
}
