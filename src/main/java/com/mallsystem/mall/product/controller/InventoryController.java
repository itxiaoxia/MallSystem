package com.mallsystem.mall.product.controller;

import com.mallsystem.mall.application.MallApplicationService;
import com.mallsystem.mall.common.api.ApiResponse;
import com.mallsystem.mall.product.dto.InventoryResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final MallApplicationService applicationService;

    public InventoryController(MallApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/{productCode}")
    public ApiResponse<InventoryResponse> getInventory(@PathVariable String productCode) {
        return ApiResponse.success(applicationService.getInventory(productCode));
    }
}
