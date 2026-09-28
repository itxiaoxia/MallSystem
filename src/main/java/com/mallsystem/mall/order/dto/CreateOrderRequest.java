package com.mallsystem.mall.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty(message = "订单至少包含一项商品")
        @Size(max = 100, message = "订单商品项不能超过100项")
        List<@Valid OrderItemRequest> items) {

    public CreateOrderRequest {
        items = items == null ? null : List.copyOf(items);
    }
}
