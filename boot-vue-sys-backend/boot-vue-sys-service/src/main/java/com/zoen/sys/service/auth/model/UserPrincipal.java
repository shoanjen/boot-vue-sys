package com.zoen.sys.service.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zone
 * @description 当前登录用户信息
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色码：1学生 2社团管理员 3平台管理员
     */
    private Integer role;

    /**
     * 账号
     */
    private String account;

    /**
     * 用户名
     */
    private String name;
}

