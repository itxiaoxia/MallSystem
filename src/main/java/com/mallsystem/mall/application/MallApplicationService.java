package com.mallsystem.mall.application;

import com.mallsystem.mall.common.api.PageResponse;
import com.mallsystem.mall.common.exception.BusinessException;
import com.mallsystem.mall.common.exception.ErrorCode;
import com.mallsystem.mall.order.dto.CreateOrderRequest;
import com.mallsystem.mall.order.dto.OrderItemResponse;
import com.mallsystem.mall.order.dto.OrderResponse;
import com.mallsystem.mall.order.dto.OrderStatusResponse;
import com.mallsystem.mall.order.entity.MallOrder;
import com.mallsystem.mall.order.entity.OrderItem;
import com.mallsystem.mall.order.entity.OrderStatus;
import com.mallsystem.mall.order.repository.MallOrderRepository;
import com.mallsystem.mall.product.dto.InventoryResponse;
import com.mallsystem.mall.product.dto.ProductQuery;
import com.mallsystem.mall.product.dto.ProductResponse;
import com.mallsystem.mall.product.entity.Product;
import com.mallsystem.mall.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
public class MallApplicationService {

    private static final DateTimeFormatter ORDER_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final ProductRepository productRepository;
    private final MallOrderRepository mallOrderRepository;

    public MallApplicationService(ProductRepository productRepository, MallOrderRepository mallOrderRepository) {
        this.productRepository = productRepository;
        this.mallOrderRepository = mallOrderRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> listProducts(ProductQuery query) {
        if (query == null || query.page() < 0 || query.size() < 1 || query.size() > 100) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT);
        }
        Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
        Page<Product> page = productRepository.searchActive(query.keyword(), PageRequest.of(query.page(), query.size(), sort));
        return new PageResponse<>(page.getContent().stream().map(this::toProductResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(String productCode) {
        String normalizedCode = normalizeRequired(productCode);
        Product product = productRepository.findByProductCode(normalizedCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        return new InventoryResponse(product.getProductCode(), product.getName(), product.getStock(), product.getStatus());
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> listOrders(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT);
        }
        Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
        Page<MallOrder> orders = mallOrderRepository.findAll(PageRequest.of(page, size, sort));
        return new PageResponse<>(orders.getContent().stream().map(this::toOrderResponse).toList(),
                orders.getNumber(), orders.getSize(), orders.getTotalElements(), orders.getTotalPages());
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT);
        }
        Map<String, Integer> quantities = mergeQuantities(request);
        List<ProductSnapshot> snapshots = new ArrayList<>(quantities.size());
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entry : quantities.entrySet()) {
            Product product = productRepository.findActiveByProductCode(entry.getKey())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND,
                            "商品不存在：" + entry.getKey()));
            if (productRepository.decrementStockIfEnough(entry.getKey(), entry.getValue()) != 1) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK,
                        "商品库存不足：" + entry.getKey());
            }
            BigDecimal lineAmount = money(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
            totalAmount = money(totalAmount.add(lineAmount));
            snapshots.add(new ProductSnapshot(product.getProductCode(), product.getName(), product.getPrice(),
                    entry.getValue(), lineAmount));
        }

        MallOrder order = new MallOrder(nextOrderNo(), totalAmount);
        snapshots.forEach(snapshot -> order.addItem(new OrderItem(snapshot.productCode(), snapshot.productName(),
                snapshot.unitPrice(), snapshot.quantity(), snapshot.lineAmount())));
        return toOrderResponse(mallOrderRepository.saveAndFlush(order));
    }

    @Transactional
    public OrderStatusResponse refundOrder(String orderNo) {
        String normalizedOrderNo = normalizeRequired(orderNo);
        MallOrder order = mallOrderRepository.findByOrderNoForUpdate(normalizedOrderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return new OrderStatusResponse(order.getOrderNo(), order.getStatus());
        }
        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.REFUND_NOT_ALLOWED);
        }

        restoreOrderStock(order);
        order.changeStatus(OrderStatus.CANCELLED);
        mallOrderRepository.saveAndFlush(order);
        return new OrderStatusResponse(order.getOrderNo(), order.getStatus());
    }

    @Transactional
    public OrderStatusResponse deleteOrder(String orderNo) {
        String normalizedOrderNo = normalizeRequired(orderNo);
        MallOrder order = mallOrderRepository.findByOrderNoForUpdate(normalizedOrderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.CLOSED) {
            throw new BusinessException(ErrorCode.ORDER_DELETE_NOT_ALLOWED);
        }
        if (order.getStatus() == OrderStatus.CREATED || order.getStatus() == OrderStatus.PAID) {
            restoreOrderStock(order);
            order.changeStatus(OrderStatus.CANCELLED);
        }

        OrderStatusResponse response = new OrderStatusResponse(order.getOrderNo(), OrderStatus.CANCELLED);
        mallOrderRepository.delete(order);
        mallOrderRepository.flush();
        return response;
    }

    @Transactional
    public OrderStatusResponse updateOrderStatus(String orderNo, OrderStatus targetStatus) {
        String normalizedOrderNo = normalizeRequired(orderNo);
        if (targetStatus == null) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT);
        }
        if (targetStatus == OrderStatus.CANCELLED) {
            return refundOrder(normalizedOrderNo);
        }
        MallOrder order = mallOrderRepository.findByOrderNo(normalizedOrderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        try {
            order.changeStatus(targetStatus);
        } catch (IllegalStateException exception) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS, exception.getMessage());
        }
        mallOrderRepository.saveAndFlush(order);
        return new OrderStatusResponse(order.getOrderNo(), order.getStatus());
    }

    private Map<String, Integer> mergeQuantities(CreateOrderRequest request) {
        Map<String, Integer> quantities = new TreeMap<>();
        request.items().forEach(item -> {
            if (item == null || item.productCode() == null || item.productCode().isBlank() || item.quantity() < 1) {
                throw new BusinessException(ErrorCode.INVALID_ARGUMENT);
            }
            try {
                quantities.merge(item.productCode(), item.quantity(), Math::addExact);
            } catch (ArithmeticException exception) {
                throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "商品数量超出范围");
            }
        });
        return quantities;
    }

    private String normalizeRequired(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT);
        }
        return value.trim();
    }

    private void restoreOrderStock(MallOrder order) {
        for (OrderItem item : order.getItems()) {
            if (productRepository.incrementStock(item.getProductCode(), item.getQuantity()) != 1) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR,
                        "库存恢复失败：" + item.getProductCode());
            }
        }
    }

    private String nextOrderNo() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "MS" + ORDER_TIME_FORMAT.format(LocalDateTime.now()) + suffix;
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(product.getProductCode(), product.getName(), product.getDescription(),
                product.getPrice(), product.getStock(), product.getStatus());
    }

    private OrderResponse toOrderResponse(MallOrder order) {
        List<OrderItemResponse> items = order.getItems().stream().map(this::toOrderItemResponse).toList();
        return new OrderResponse(order.getOrderNo(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt(), items);
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(item.getProductCode(), item.getProductName(), item.getUnitPrice(),
                item.getQuantity(), item.getLineAmount());
    }

    private record ProductSnapshot(String productCode, String productName, BigDecimal unitPrice,
                                   int quantity, BigDecimal lineAmount) {
    }
}
