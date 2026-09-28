package com.mallsystem.mall.product.controller;

import com.mallsystem.mall.application.MallApplicationService;
import com.mallsystem.mall.common.api.ApiResponse;
import com.mallsystem.mall.common.api.PageResponse;
import com.mallsystem.mall.product.dto.ProductQuery;
import com.mallsystem.mall.product.dto.ProductResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final MallApplicationService applicationService;

    public ProductController(MallApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> listProducts(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) @Size(max = 64) String keyword) {
        return ApiResponse.success(applicationService.listProducts(new ProductQuery(page, size, keyword)));
    }
}
