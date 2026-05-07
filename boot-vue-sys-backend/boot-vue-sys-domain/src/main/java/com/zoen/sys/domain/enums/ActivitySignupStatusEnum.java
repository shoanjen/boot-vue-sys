package com.zoen.sys.domain.enums;

import lombok.Getter;

/**
 * @author zone
 * @description 活动报名状态枚举
 * @since 2026-05-01
 */
@Getter
public enum ActivitySignupStatusEnum {
    SIGNED(1, "已报名"),
    CANCELED(2, "已取消");

    private final int code;
    private final String desc;

    ActivitySignupStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

