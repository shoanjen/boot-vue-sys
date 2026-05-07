package com.zoen.sys.domain.enums;

import lombok.Getter;

/**
 * @author zone
 * @description 用户角色枚举
 * @since 2026-05-01
 */
@Getter
public enum UserRoleEnum {
    STUDENT(1, "学生"),
    CLUB_ADMIN(2, "社团管理员"),
    PLATFORM_ADMIN(3, "平台管理员");

    public static final int STUDENT_CODE = 1;
    public static final int CLUB_ADMIN_CODE = 2;
    public static final int PLATFORM_ADMIN_CODE = 3;

    private final int code;
    private final String desc;

    UserRoleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * @param code 角色码
     * @description 通过角色码获取枚举
     * @return 用户角色枚举
     */
    public static UserRoleEnum of(int code) {
        for (UserRoleEnum x : values()) {
            if (x.code == code) {
                return x;
            }
        }
        return null;
    }
}
