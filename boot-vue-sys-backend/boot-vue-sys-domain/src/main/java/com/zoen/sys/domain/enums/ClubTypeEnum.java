package com.zoen.sys.domain.enums;

import lombok.Getter;

/**
 * @author zone
 * @description 社团类型枚举
 * @since 2026-05-01
 */
@Getter
public enum ClubTypeEnum {
    ACADEMIC(1, "学术"),
    SPORTS(2, "文体"),
    PUBLIC_WELFARE(3, "公益"),
    INTEREST(4, "兴趣");

    private final int code;
    private final String desc;

    ClubTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

