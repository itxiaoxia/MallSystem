package com.mallsystem.mall.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(
        name = "order_items",
        indexes = {
                @Index(name = "idx_order_items_order_id", columnList = "order_id"),
                @Index(name = "idx_order_items_product_code", columnList = "product_code")
        }
)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private MallOrder order;

    @Column(name = "product_code", nullable = false, length = 64)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 128)
    private String productName;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "line_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal lineAmount;

    protected OrderItem() {
    }

    public OrderItem(String productCode, String productName, BigDecimal unitPrice,
                     int quantity, BigDecimal lineAmount) {
        this.productCode = productCode;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineAmount = lineAmount;
    }

    void assignTo(MallOrder order) {
        this.order = order;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineAmount() {
        return lineAmount;
    }
}
