package com.mallsystem.mall.order.controller;

import com.mallsystem.mall.application.MallApplicationService;
import com.mallsystem.mall.common.api.ApiResponse;
import com.mallsystem.mall.order.dto.CreateOrderRequest;
import com.mallsystem.mall.order.dto.OrderResponse;
import com.mallsystem.mall.order.dto.OrderStatusResponse;
import com.mallsystem.mall.order.dto.UpdateOrderStatusRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final MallApplicationService applicationService;

    public OrderController(MallApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(applicationService.createOrder(request));
    }

    @PutMapping("/{orderNo}/status")
    public ApiResponse<OrderStatusResponse> updateStatus(
            @PathVariable String orderNo,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ApiResponse.success(applicationService.updateOrderStatus(orderNo, request.status()));
    }
}
