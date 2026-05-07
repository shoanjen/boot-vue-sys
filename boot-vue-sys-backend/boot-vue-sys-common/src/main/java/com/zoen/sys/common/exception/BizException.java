package com.zoen.sys.common.exception;

import lombok.Getter;

/**
 * @author zone
 * @description 业务异常
 * @since 2026-05-01
 */
@Getter
public class BizException extends RuntimeException {

    private final String code;

    /**
     * @param errorCode 错误码
     * @description 构建业务异常
     * @return 无
     */
    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * @param errorCode 错误码
     * @param message 自定义错误信息
     * @description 构建业务异常
     * @return 无
     */
    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
