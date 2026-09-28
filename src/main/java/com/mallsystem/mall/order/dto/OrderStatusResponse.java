package com.mallsystem.mall.order.dto;

import com.mallsystem.mall.order.entity.OrderStatus;

public record OrderStatusResponse(String orderNo, OrderStatus status) {
}
