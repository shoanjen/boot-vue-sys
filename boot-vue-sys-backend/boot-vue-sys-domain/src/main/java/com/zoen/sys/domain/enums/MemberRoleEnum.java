package com.zoen.sys.domain.enums;

import lombok.Getter;

/**
 * @author zone
 * @description 社团成员角色枚举
 * @since 2026-05-01
 */
@Getter
public enum MemberRoleEnum {
    MEMBER(1, "成员"),
    ADMIN(2, "管理员");

    private final int code;
    private final String desc;

    MemberRoleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

