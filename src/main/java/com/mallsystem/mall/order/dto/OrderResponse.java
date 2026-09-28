package com.mallsystem.mall.order.dto;

import com.mallsystem.mall.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String orderNo,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<OrderItemResponse> items) {

    public OrderResponse {
        items = items == null ? List.of() : List.copyOf(items);
    }
}
