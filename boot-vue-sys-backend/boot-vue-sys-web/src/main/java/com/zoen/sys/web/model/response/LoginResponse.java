package com.zoen.sys.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zone
 * @description 登录响应
 * @since 2026-05-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserResponse user;
}

