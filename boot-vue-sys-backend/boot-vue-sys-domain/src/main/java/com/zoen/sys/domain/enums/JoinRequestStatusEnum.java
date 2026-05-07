package com.zoen.sys.domain.enums;

import lombok.Getter;

/**
 * @author zone
 * @description 入社申请状态枚举
 * @since 2026-05-01
 */
@Getter
public enum JoinRequestStatusEnum {
    PENDING(0, "待审核"),
    APPROVED(1, "通过"),
    REJECTED(2, "驳回"),
    CANCELED(3, "取消");

    private final int code;
    private final String desc;

    JoinRequestStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

