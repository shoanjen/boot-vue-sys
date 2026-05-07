package com.zoen.sys.domain.enums;

import lombok.Getter;

/**
 * @author zone
 * @description 公告状态枚举
 * @since 2026-05-01
 */
@Getter
public enum AnnouncementStatusEnum {
    PUBLISHED(1, "已发布"),
    OFFLINE(2, "已下架");

    private final int code;
    private final String desc;

    AnnouncementStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

