package com.mallsystem.mall.common.exception;

public enum ErrorCode {
    INVALID_ARGUMENT("INVALID_ARGUMENT", "参数不合法"),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "商品不存在"),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "订单不存在"),
    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", "商品库存不足"),
    INVALID_ORDER_STATUS("INVALID_ORDER_STATUS", "订单状态不允许变更"),
    REFUND_NOT_ALLOWED("REFUND_NOT_ALLOWED", "当前订单状态不允许退款"),
    ORDER_DELETE_NOT_ALLOWED("ORDER_DELETE_NOT_ALLOWED", "当前订单状态不允许删除"),
    WRITE_CONFIRMATION_REQUIRED("WRITE_CONFIRMATION_REQUIRED", "写操作需要明确确认"),
    INTERNAL_ERROR("INTERNAL_ERROR", "服务器内部错误");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
