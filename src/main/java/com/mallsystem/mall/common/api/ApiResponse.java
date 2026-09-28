package com.mallsystem.mall.common.api;

import com.mallsystem.mall.common.exception.ErrorCode;

public record ApiResponse<T>(String code, String message, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("OK", "操作成功", data);
    }

    public static <T> ApiResponse<T> failure(ErrorCode errorCode, String message) {
        return new ApiResponse<>(errorCode.code(), message, null);
    }
}
