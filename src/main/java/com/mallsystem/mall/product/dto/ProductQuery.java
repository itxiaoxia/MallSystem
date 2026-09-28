package com.mallsystem.mall.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ProductQuery(
        @Min(value = 0, message = "页码不能小于0") int page,
        @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") int size,
        @Size(max = 64, message = "关键词不能超过64个字符") String keyword) {

    public ProductQuery {
        keyword = keyword == null ? null : keyword.trim();
    }
}
