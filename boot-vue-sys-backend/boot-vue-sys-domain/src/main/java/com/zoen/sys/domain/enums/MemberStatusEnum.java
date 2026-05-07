package com.zoen.sys.domain.enums;

import lombok.Getter;

/**
 * @author zone
 * @description 成员状态枚举
 * @since 2026-05-01
 */
@Getter
public enum MemberStatusEnum {
    ACTIVE(1, "正常"),
    REMOVED(2, "已移除");

    private final int code;
    private final String desc;

    MemberStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

