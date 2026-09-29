package com.mallsystem.mall.order.controller;

import com.mallsystem.mall.application.MallApplicationService;
import com.mallsystem.mall.common.api.ApiResponse;
import com.mallsystem.mall.common.api.PageResponse;
import com.mallsystem.mall.order.dto.CreateOrderRequest;
import com.mallsystem.mall.order.dto.OrderResponse;
import com.mallsystem.mall.order.dto.OrderStatusResponse;
import com.mallsystem.mall.order.dto.UpdateOrderStatusRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Validated
public class OrderController {

    private final MallApplicationService applicationService;

    public OrderController(MallApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(applicationService.createOrder(request));
    }

    @GetMapping
    public ApiResponse<PageResponse<OrderResponse>> listOrders(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.success(applicationService.listOrders(page, size));
    }

    @PostMapping("/{orderNo}/refund")
    public ApiResponse<OrderStatusResponse> refundOrder(@PathVariable String orderNo) {
        return ApiResponse.success(applicationService.refundOrder(orderNo));
    }

    @DeleteMapping("/{orderNo}")
    public ApiResponse<OrderStatusResponse> deleteOrder(@PathVariable String orderNo) {
        return ApiResponse.success(applicationService.deleteOrder(orderNo));
    }

    @PutMapping("/{orderNo}/status")
    public ApiResponse<OrderStatusResponse> updateStatus(
            @PathVariable String orderNo,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ApiResponse.success(applicationService.updateOrderStatus(orderNo, request.status()));
    }
}
