package com.mallsystem.mall.order.dto;

import com.mallsystem.mall.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull(message = "订单状态不能为空") OrderStatus status) {
}
