package com.mallsystem.mall.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrderItemRequest(
        @NotBlank(message = "商品编码不能为空") @Size(max = 64, message = "商品编码不能超过64个字符") String productCode,
        @Min(value = 1, message = "商品数量必须大于0") @Max(value = 10000, message = "单项商品数量不能超过10000") int quantity) {

    public OrderItemRequest {
        productCode = productCode == null ? null : productCode.trim();
    }
}
