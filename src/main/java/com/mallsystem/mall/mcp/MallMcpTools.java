package com.mallsystem.mall.mcp;

import com.mallsystem.mall.application.MallApplicationService;
import com.mallsystem.mall.common.api.PageResponse;
import com.mallsystem.mall.common.exception.BusinessException;
import com.mallsystem.mall.common.exception.ErrorCode;
import com.mallsystem.mall.order.dto.CreateOrderRequest;
import com.mallsystem.mall.order.dto.OrderResponse;
import com.mallsystem.mall.order.dto.OrderStatusResponse;
import com.mallsystem.mall.order.entity.OrderStatus;
import com.mallsystem.mall.product.dto.InventoryResponse;
import com.mallsystem.mall.product.dto.ProductQuery;
import com.mallsystem.mall.product.dto.ProductResponse;
import org.springframework.stereotype.Component;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;

@Component
public class MallMcpTools {

    private final MallApplicationService applicationService;

    public MallMcpTools(MallApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @McpTool(name = "list_products", description = "分页查询在售商品")
    public PageResponse<ProductResponse> listProducts(
            @McpToolParam(description = "从0开始的页码", required = true) int page,
            @McpToolParam(description = "每页数量，范围1到100", required = true) int size,
            @McpToolParam(description = "按商品编码或名称筛选，可为空", required = false) String keyword) {
        return applicationService.listProducts(new ProductQuery(page, size, keyword));
    }

    @McpTool(name = "get_inventory", description = "查询商品当前库存")
    public InventoryResponse getInventory(
            @McpToolParam(description = "商品编码", required = true) String productCode) {
        return applicationService.getInventory(productCode);
    }

    @McpTool(name = "create_order", description = "在明确确认后创建订单并扣减库存")
    public OrderResponse createOrder(
            @McpToolParam(description = "必须明确传入true才允许写入", required = true) boolean confirmed,
            @McpToolParam(description = "订单商品明细", required = true) CreateOrderRequest request) {
        requireWriteConfirmation(confirmed);
        return applicationService.createOrder(request);
    }

    @McpTool(name = "update_order_status", description = "在明确确认后更新订单状态")
    public OrderStatusResponse updateOrderStatus(
            @McpToolParam(description = "订单号", required = true) String orderNo,
            @McpToolParam(description = "目标状态：PAID、SHIPPED、CLOSED或CANCELLED", required = true)
            OrderStatus status,
            @McpToolParam(description = "必须明确传入true才允许写入", required = true) boolean confirmed) {
        requireWriteConfirmation(confirmed);
        return applicationService.updateOrderStatus(orderNo, status);
    }

    private void requireWriteConfirmation(boolean confirmed) {
        if (!confirmed) {
            throw new BusinessException(ErrorCode.WRITE_CONFIRMATION_REQUIRED);
        }
    }
}
