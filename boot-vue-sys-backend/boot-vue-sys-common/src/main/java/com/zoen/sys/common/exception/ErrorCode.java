package com.zoen.sys.common.exception;

import lombok.Getter;

/**
 * @author zone
 * @description 错误码定义
 * @since 2026-05-01
 */
@Getter
public enum ErrorCode {

    PARAM_INVALID("PARAM_INVALID", "参数校验失败"),
    BIZ_ERROR("BIZ_ERROR", "业务异常"),
    UNAUTHORIZED("UNAUTHORIZED", "未授权访问"),
    FORBIDDEN("FORBIDDEN", "无权限访问"),
    NOT_FOUND("NOT_FOUND", "资源不存在"),
    INTERNAL_ERROR("INTERNAL_ERROR", "系统异常");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
