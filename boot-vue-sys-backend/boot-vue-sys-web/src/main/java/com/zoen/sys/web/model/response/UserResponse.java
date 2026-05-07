package com.zoen.sys.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zone
 * @description 用户响应
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 账号
     */
    private String account;

    /**
     * 用户名
     */
    private String name;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色码
     */
    private Integer role;
}

