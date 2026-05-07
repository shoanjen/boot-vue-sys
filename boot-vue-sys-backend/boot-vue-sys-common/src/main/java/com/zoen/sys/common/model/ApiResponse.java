package com.zoen.sys.common.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * @author zone
 * @description 统一接口响应体
 * @since 2026-05-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private boolean success;

    private String code;

    private String message;

    private T data;

    private Instant timestamp;

    /**
     * @param data 业务数据
     * @description 成功响应
     * @return 统一响应体
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "0", "OK", data, Instant.now());
    }

    /**
     * @param code 错误码
     * @param message 错误信息
     * @description 失败响应
     * @return 统一响应体
     */
    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<>(false, code, message, null, Instant.now());
    }
}
