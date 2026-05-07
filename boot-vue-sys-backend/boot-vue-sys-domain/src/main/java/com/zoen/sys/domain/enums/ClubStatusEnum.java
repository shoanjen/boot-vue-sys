package com.zoen.sys.domain.enums;

import lombok.Getter;

/**
 * @author zone
 * @description 社团状态枚举
 * @since 2026-05-01
 */
@Getter
public enum ClubStatusEnum {
    PENDING(0, "待审"),
    APPROVED(1, "上架"),
    REJECTED(2, "驳回"),
    OFFLINE(3, "下架");

    private final int code;
    private final String desc;

    ClubStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

